package com.github.restup.controller.request.parser.params;

import static com.github.restup.controller.request.parser.params.ComposedRequestParamParser.PAGE_NUMBER;
import static com.github.restup.controller.request.parser.params.ComposedRequestParamParser.pageNumber;
import static com.github.restup.controller.request.parser.params.ParameterParser.ParameterParsers.Bracketed;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.github.restup.controller.request.parser.RequestParamParser;
import com.github.restup.controller.request.parser.params.ParameterValueParser.ParameterValueParsers;
import com.github.restup.errors.RequestError;
import com.github.restup.errors.RequestError.Builder;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class PageNumberRequestParamParserTest extends AbstractRequestParamParserTest {

    protected RequestParamParser parser = pageNumber().build();
    protected RequestParamParser parserBrackets = pageNumber(Bracketed).build();

    @Override
    protected String getParameterName() {
        return PAGE_NUMBER;
    }

    @Test
    public void testAccept() {
        assertTrue(parser.accept("pageNumber"));
        assertFalse(parser.accept("pagNumber"));
        assertFalse(parser.accept("pageNumber[foo]"));
    }

    @Test
    public void testAcceptBrackets() {
        assertFalse(parserBrackets.accept("pageNumber"));
        assertFalse(parserBrackets.accept("pegNumber"));
        assertTrue(parserBrackets.accept("pageNumber[foo]"));
    }

    @Test
    public void testParameterValueParser() {
        ParameterValueParsers.PAGE_NUMBER.parse(ctx, result, "  10 ", "10");
        verify(builder).setPageOffset(getParameterName(), 9, true);
        verifyParameterValueParser();
    }

    @Test
    public void testParameterValueParserError() {
        ParameterValueParsers.PAGE_NUMBER.parse(ctx, result, "  ten ", "ten");
        verifyParameterValueParserError("  ten ");
    }

    @Test
    public void testRequestParamParser() {
        parser.parse(request, builder, getParameterName(), " 20 ");
        verify(builder).setPageOffset(getParameterName(), 19, true);
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
        verify(builder).setPageOffset(getParameterName(), 9, true);

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
