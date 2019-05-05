package com.github.restup.controller.request.parser.params;

import com.github.restup.registry.Resource;

public class ParameterParserResult {

    private final Resource resource;
    private final String[] tokens;

    private ParameterParserResult(Resource resource, String[] tokens) {
        this.resource = resource;
        this.tokens = tokens;
    }

    public static ParameterParserResult of(Resource resource, String... tokens) {
        return new ParameterParserResult(resource, tokens);
    }

    public Resource getResource() {
        return resource;
    }

    public String[] getTokens() {
        return tokens;
    }

}
