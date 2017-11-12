package com.github.restup.controller.request.parser.params;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class PageLimitParserTest {

    @Test
    public void testAccept() {
        PageLimitParser parser = new PageLimitParser();
        assertTrue(parser.accept("limit"));
        assertFalse(parser.accept("tot"));
        assertFalse(parser.accept("limit[foo]"));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void testParse() {
        String param = "limit";
        ParsedResourceControllerRequest.Builder b = Mockito.mock(ParsedResourceControllerRequest.Builder.class);
        PageLimitParser parser = new PageLimitParser(param);
        parser.parse(null, b, param, new String[]{"100", "200", null, "  ", ""});
        verify(b, times(1)).setPageLimit(param, 100);
        verify(b, times(1)).setPageLimit(param, 200);
        verify(b, times(2)).setPageLimit(any(String.class), any(Integer.class));
        verify(b, times(1)).addParameterError(parser.getParameterName(), null);
        verify(b, times(1)).addParameterError(parser.getParameterName(), "");
        verify(b, times(1)).addParameterError(parser.getParameterName(), "  ");
    }

}
