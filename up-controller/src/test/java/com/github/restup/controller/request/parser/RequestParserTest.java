package com.github.restup.controller.request.parser;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class RequestParserTest {

    @Test
    public void testBuilder() {
        RequestParserChain parser = (RequestParserChain) RequestParser.builder()
                .fieldsParamName("a")
                .filterParamName("b")
                .includeParamName("i")
                .pageLimitParamName("l")
                .pageOffsetParamName("o")
                .pageNumberParamName("n")
                .sortParamName("s")
                .autoDetectDisabled(true)
                .defaultMediaType("application/json")
                .relationshipParser(new DefaultRelationshipsParser())
                .requestParamParsers()
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
