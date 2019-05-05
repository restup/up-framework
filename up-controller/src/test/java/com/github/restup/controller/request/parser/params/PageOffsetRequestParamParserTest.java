package com.github.restup.controller.request.parser.params;

import static com.github.restup.controller.request.parser.params.ComposedRequestParamParser.OFFSET;
import static com.github.restup.controller.request.parser.params.ComposedRequestParamParser.offset;
import static com.github.restup.controller.request.parser.params.ParameterParser.ParameterParsers.Bracketed;
import static com.github.restup.controller.request.parser.params.ParameterValueParser.ParameterValueParsers.PAGE_OFFSET;
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

public class PageOffsetRequestParamParserTest extends AbstractRequestParamParserTest {

    protected RequestParamParser parser = offset().build();
    protected RequestParamParser parserBrackets = offset(Bracketed).build();

    @Override
    protected String getParameterName() {
        return OFFSET;
    }

    @Test
    public void testAccept() {
        assertTrue(parser.accept("offset"));
        assertFalse(parser.accept("ofset"));
        assertFalse(parser.accept("offset[foo]"));
    }

    @Test
    public void testAcceptBrackets() {
        assertFalse(parserBrackets.accept("offset"));
        assertFalse(parserBrackets.accept("ofset"));
        assertTrue(parserBrackets.accept("offset[foo]"));
    }

    @Test
    public void testParameterValueParser() {
        PAGE_OFFSET.parse(ctx, result, "  10 ", "10");
        verify(builder).setPageOffset(getParameterName(), 10);
        verifyParameterValueParser();
    }

    @Test
    public void testParameterValueParserError() {
        PAGE_OFFSET.parse(ctx, result, "  ten ", "ten");
        verifyParameterValueParserError("  ten ");
    }

    @Test
    public void testRequestParamParser() {
        parser.parse(request, builder, getParameterName(), " 20 ");
        verify(builder).setPageOffset(getParameterName(), 20);
        verifyRequestParamParser();
    }

    @Test
    public void testRequestParamParserError() {
        parser.parse(request, builder, getParameterName(), "  twenty ");
        verifyRequestParamParserError("  twenty ");
    }

    @Test
    public void testRequestParamParserUntrimmedError() {
        RequestParamParser parser = offset().trimValues(false).build();
        parser.parse(request, builder, getParameterName(), " 20 ");
        verifyRequestParamParserError(" 20 ");
    }

    @Test
    public void testRequestParamParserMultipleError() {
        String[] values = new String[]{"10", " 20 "};
        parser.parse(request, builder, getParameterName(), values);
        verify(builder).setPageOffset(getParameterName(), 10);

        ArgumentCaptor<Builder> captor = ArgumentCaptor.forClass(RequestError.Builder.class);
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
