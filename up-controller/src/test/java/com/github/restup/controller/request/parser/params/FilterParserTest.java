package com.github.restup.controller.request.parser.params;

import com.github.restup.controller.model.ParsedResourceControllerRequest.Builder;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class FilterParserTest {

    @Test
    public void testAccept() {
        FilterParser parser = new FilterParser();
        assertFalse(parser.accept("filter"));
        assertTrue(parser.accept("filter[foo]"));
        assertTrue(parser.accept("filter[foo][gt]"));
        assertFalse(parser.accept("tot"));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void testParse() {
        String param = "filter[foo]";
        Builder b = Mockito.mock(Builder.class);
        FilterParser parser = new FilterParser(param);
        parser.parse(null, b, param, new String[]{"a", "b", null, "  ", ""});
        verify(b, times(1)).addFilter(param, "a", "foo", (String) null, "a");
        verify(b, times(1)).addFilter(param, "b", "foo", (String) null, "b");
        verify(b, times(2)).addFilter(any(String.class), any(String.class), any(String.class), nullable(String.class), any(String.class));
        verify(b, times(1)).addParameterError(parser.getParameterName(), null);
        verify(b, times(1)).addParameterError(parser.getParameterName(), "");
        verify(b, times(1)).addParameterError(parser.getParameterName(), "  ");
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void testParseFunction() {
        String param = "filter[foo][gt]";
        Builder b = Mockito.mock(Builder.class);
        FilterParser parser = new FilterParser(param);
        parser.parse(null, b, param, new String[]{"a", "b", null, "  ", ""});
        verify(b, times(1)).addFilter(param, "a", "foo", "gt", "a");
        verify(b, times(1)).addFilter(param, "b", "foo", "gt", "b");
        verify(b, times(2)).addFilter(any(String.class), any(String.class), any(String.class), any(String.class), any(String.class));

        verify(b, times(1)).addParameterError(parser.getParameterName(), null);
        verify(b, times(1)).addParameterError(parser.getParameterName(), "");
        verify(b, times(1)).addParameterError(parser.getParameterName(), "  ");
    }

    @Test
    public void testParseBad() {
        bad("filter[foo");
        bad("filter[foo][");
        bad("filter[]");
        bad("filter[foo][]");
        bad("filter[foo][bar");
        bad("filter[foo][bar][");
        bad("filter[foo][bar][]");
        bad("filter[foo][bar][boo]");
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void bad(String param) {
        Builder b = Mockito.mock(Builder.class);
        FilterParser parser = new FilterParser(param);
        parser.parse(null, b, param, new String[]{"a"});
        verify(b, times(0)).addFilter(any(String.class), any(String.class), any(String.class), nullable(String.class), any(String.class));
        verify(b, times(1)).addParameterError(any(String.class), any(Object.class));
    }

}
