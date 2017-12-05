package com.github.restup.controller.request.parser;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.request.parser.params.PageLimitParser;
import com.github.restup.controller.request.parser.params.PageNumberParser;
import com.github.restup.controller.request.parser.params.PageOffsetParser;
import com.github.restup.controller.request.parser.params.SortParamParser;
import java.util.Arrays;
import org.junit.Test;
import org.mockito.Mockito;

public class ParameterParserChainTest {

    @SuppressWarnings({"rawtypes"})
    @Test
    public void testParse() {
        ParsedResourceControllerRequest.Builder b = Mockito.mock(ParsedResourceControllerRequest.Builder.class);
        ResourceControllerRequest details = Mockito.mock(ResourceControllerRequest.class);
        when(details.getParameterNames()).thenReturn(Arrays.asList("offset", "limit", "pageNumber", "sort"));
        when(details.getParameter("offset")).thenReturn(new String[]{"2"});
        when(details.getParameter("limit")).thenReturn(new String[]{"100", "A"});
//		test that pageNumber & sort return null to cover null parameter
        ParameterParserChain chain = new ParameterParserChain(new PageOffsetParser(), new PageLimitParser(), new PageNumberParser(), new SortParamParser());
        chain.parse(details, b);
        verify(b, times(1)).setPageOffset("offset", 2);
        verify(b, times(1)).setPageLimit("limit", 100);
        verify(b, times(1)).addParameterError("limit", "A");
        verify(b, times(1)).addParameterError("pageNumber", null);
        verify(b, times(1)).addParameterError("sort", null);
    }

    @SuppressWarnings({"rawtypes"})
    @Test
    public void testNulls() {
        ParsedResourceControllerRequest.Builder b = Mockito.mock(ParsedResourceControllerRequest.Builder.class);
        ResourceControllerRequest details = Mockito.mock(ResourceControllerRequest.class);
        when(details.getParameterNames()).thenReturn(null);
        ParameterParserChain chain = new ParameterParserChain();
        chain.parse(details, b);
    }
}
