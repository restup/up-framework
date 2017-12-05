package com.github.restup.controller.request.parser.params;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import org.junit.Test;
import org.mockito.Mockito;

public class PageOffsetParserTest {

    @Test
    public void testAccept() {
        PageOffsetParser parser = new PageOffsetParser();
        assertTrue(parser.accept("offset"));
        assertFalse(parser.accept("ofset"));
        assertFalse(parser.accept("offset[foo]"));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void testParse() {
        String param = "offset";
        ParsedResourceControllerRequest.Builder b = Mockito.mock(ParsedResourceControllerRequest.Builder.class);
        PageOffsetParser parser = new PageOffsetParser();
        parser.parse(null, b, param, new String[]{"1", "2", null, "  ", ""});
        verify(b, times(1)).setPageOffset(parser.getParameterName(), 1);
        verify(b, times(1)).setPageOffset(parser.getParameterName(), 2);
        verify(b, times(2)).setPageOffset(any(String.class), any(Integer.class));
        verify(b, times(1)).addParameterError(parser.getParameterName(), null);
        verify(b, times(1)).addParameterError(parser.getParameterName(), "");
        verify(b, times(1)).addParameterError(parser.getParameterName(), "  ");
    }

}
