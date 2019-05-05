package com.github.restup.controller.request.parser.params;

import static com.github.restup.controller.request.parser.params.ComposedRequestParamParser.LIMIT;
import static com.github.restup.controller.request.parser.params.ComposedRequestParamParser.limit;
import static com.github.restup.controller.request.parser.params.ParameterParser.ParameterParsers.Bracketed;
import static com.github.restup.controller.request.parser.params.ParameterValueParser.ParameterValueParsers.PAGE_LIMIT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.github.restup.controller.request.parser.RequestParamParser;
import com.github.restup.errors.RequestError;
import com.github.restup.errors.RequestError.Builder;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class PageLimitRequestParamParserTest extends AbstractRequestParamParserTest {

    protected RequestParamParser parser = limit().build();
    protected RequestParamParser parserBrackets = limit(Bracketed).build();

    @Override
    protected String getParameterName() {
        return LIMIT;
    }

    @Test
    public void testAccept() {
        assertTrue(parser.accept("limit"));
        assertFalse(parser.accept("limmit"));
        assertFalse(parser.accept("limit[foo]"));
    }

    @Test
    public void testAcceptBrackets() {
        assertFalse(parserBrackets.accept("limit"));
        assertFalse(parserBrackets.accept("limmit"));
        assertTrue(parserBrackets.accept("limit[foo]"));
    }

    @Test
    public void testParameterValueParser() {
        PAGE_LIMIT.parse(ctx, result, "  10 ", "10");
        verify(builder).setPageLimit(getParameterName(), 10);
        verifyParameterValueParser();
    }

    @Test
    public void testParameterValueParserError() {
        PAGE_LIMIT.parse(ctx, result, "  ten ", "ten");
        verifyParameterValueParserError("  ten ");
    }

    @Test
    public void testRequestParamParser() {
        parser.parse(request, builder, getParameterName(), " 20 ");
        verify(builder).setPageLimit(getParameterName(), 20);
        verifyRequestParamParser();
    }

    @Test
    public void testRequestParamParserError() {
        parser.parse(request, builder, getParameterName(), "  twenty ");
        verifyRequestParamParserError("  twenty ");
    }

    @Test
    public void testRequestParamParserMultipleError() {
        String[] values = new String[]{"10", " 20 "};
        parser.parse(request, builder, getParameterName(), values);
        verify(builder).setPageLimit(getParameterName(), 10);

        ArgumentCaptor<Builder> captor = ArgumentCaptor.forClass(Builder.class);
        verify(builder).addParameterError(captor.capture(), any(), any());
        RequestError error = captor.getValue()
            .resource(requestedResource) // simulate addError
            .build();

        assertEquals("INVALID_PARAMETER_OCCURRENCES", error.getCode());
        assertEquals("Parameter may only occur once per request", error.getTitle());
        assertEquals("'" + getParameterName() + "' expected once and received 2 times",
            error.getDetail());

        assertParameterError(error, getParameterName(), values);
        assertParameterErrorMeta(error, getParameterName(), values);

        verify(requestedResource, times(2)).getName();
    }

}
