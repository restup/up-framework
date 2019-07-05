package com.github.restup.controller.request.parser;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import com.github.restup.config.ConfigurationContext;
import org.junit.Test;

public class RequestParserTest {

    @Test
    public void testBuilder() {
        RequestParserChain parser = (RequestParserChain) RequestParser.builder()
            .requestParamParser(RequestParamParser.builder()
                .withFieldsNamed("a")
                .withFilterNamed("b")
                .withIncludeNamed("i")
                .withPageLimitNamed("l")
                .withPageOffsetNamed("o")
                .withPageNumberNamed("n")
                .withSortNamed("s"))
            .autoDetectDisabled(true)
            .defaultMediaType("application/json")
            .relationshipParser(new DefaultRelationshipsParser())
            .configurationContext(mock(ConfigurationContext.class))
            .build();

        assertAccepts(parser, true, "a", "i", "l", "o", "s", "n");
        assertAccepts(parser, false, "include", "limit", "offset", "sort", "pageNumber");
    }

    private void assertAccepts(RequestParserChain parser, boolean expected, String... params) {
        for (String param : params) {
            assertEquals(param, expected, accepts(parser, param));
        }
    }

    private boolean accepts(RequestParserChain parser, String param) {
        for (RequestParser p : parser.getParsers()) {
            if (p instanceof ParameterParserChain) {
                return accepts((ParameterParserChain) p, param);
            }
        }
        return false;
    }

    private boolean accepts(ParameterParserChain chain, String param) {
        for (RequestParamParser p : chain.getParsers()) {
            if (p.accept(param)) {
                return true;
            }
        }
        return false;
    }

}
