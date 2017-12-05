package com.github.restup.controller.request.parser.params;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import org.junit.Test;
import org.mockito.Mockito;

public class PageNumberParserTest {

    @Test
    public void testAccept() {
        PageNumberParser parser = new PageNumberParser();
        assertTrue(parser.accept("pageNumber"));
        assertFalse(parser.accept("ofset"));
        assertFalse(parser.accept("pageNumber[foo]"));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void testParse() {
        String param = "page";
        ParsedResourceControllerRequest.Builder b = Mockito.mock(ParsedResourceControllerRequest.Builder.class);
        PageNumberParser parser = new PageNumberParser(param, true, true);
        parser.parse(null, b, param, new String[]{"0", "1", "2", null, "  ", ""});
        verify(b, times(2)).setPageOffset(param, 0, true);
        verify(b, times(1)).setPageOffset(param, 1, true);
        verify(b, times(3)).setPageOffset(any(String.class), any(Integer.class), any(Boolean.class));
        verify(b, times(0)).addParameterError(parser.getParameterName(), null);
    }

}
