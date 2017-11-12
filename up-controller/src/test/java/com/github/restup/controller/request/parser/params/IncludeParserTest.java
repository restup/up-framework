package com.github.restup.controller.request.parser.params;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.query.ResourceQueryStatement.Type;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class IncludeParserTest {

    @Test
    public void testAccept() {
        IncludeParser parser = new IncludeParser();
        assertTrue(parser.accept("query"));
        assertFalse(parser.accept("field"));
        assertFalse(parser.accept("query[foo]"));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void testParse() {
        String paramName = "query";
        ParsedResourceControllerRequest.Builder b = Mockito.mock(ParsedResourceControllerRequest.Builder.class);

        IncludeParser parser = new IncludeParser(paramName);
        String paramValue = "bar,foo[barId]";
        parser.parse(null, b, paramName, new String[]{paramValue, null, "  ", ""});
        verify(b, times(1)).setFieldRequest(paramName, paramValue, "bar", Type.Default);
        verify(b, times(1)).setFieldRequest(paramName, paramValue, "foo", Type.Default);
        verify(b, times(1)).addIncludeJoinPaths(paramName, paramValue, "foo", "barId");
        verify(b, times(1)).addParameterError(parser.getParameterName(), null);
        verify(b, times(1)).addParameterError(parser.getParameterName(), "");
        verify(b, times(1)).addParameterError(parser.getParameterName(), "  ");
    }

}
