package com.github.restup.controller.request.parser.params;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.github.restup.controller.model.ParsedResourceControllerRequest.Builder;
import org.junit.Test;
import org.mockito.Mockito;

public class SortParamParserTest {

    @Test
    public void testAccept() {
        SortParamParser parser = new SortParamParser();
        assertTrue(parser.accept("sort"));
        assertFalse(parser.accept("sord"));
        assertFalse(parser.accept("sort[foo]"));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void testParse() {
        String param = "sort";
        Builder b = Mockito.mock(Builder.class);
        SortParamParser parser = new SortParamParser();
        parser.parse(null, b, param, new String[]{"a,,-b", "d,  ,+c", null});
        verify(b, times(1)).addSort(param, "a,,-b", "a", null);
        verify(b, times(1)).addSort(param, "a,,-b", "b", false);
        verify(b, times(1)).addSort(param, "d,  ,+c", "c", true);
        verify(b, times(1)).addSort(param, "d,  ,+c", "d", null);
        verify(b, times(4)).addSort(any(String.class), any(String.class), any(String.class), (Boolean) any());
        verify(b, times(1)).addParameterError(parser.getParameterName(), null);
        verify(b, times(1)).addParameterError(parser.getParameterName(), "a,,-b");
        verify(b, times(1)).addParameterError(parser.getParameterName(), "d,  ,+c");
    }

}
