package com.github.restup.controller.request.parser;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.request.parser.path.RequestPathParserResult;
import com.github.restup.util.Assert;

/**
 * Iterates over an array of {@link RequestParser}s executing {@link #parse(ResourceControllerRequest, RequestPathParserResult, ParsedResourceControllerRequest.Builder)} for each
 *
 * @author abuttaro
 */
public class RequestParserChain implements RequestParser {

    private final RequestParser[] parsers;

    public RequestParserChain(RequestParser... parsers) {
        super();
        Assert.notEmpty("parsers are required", parsers);
        this.parsers = parsers;
    }

    @Override
    public void parse(ResourceControllerRequest request,
        RequestPathParserResult requestPathParserResult,
        ParsedResourceControllerRequest.Builder<?> builder) {
        for (RequestParser parser : parsers) {
            parser.parse(request, requestPathParserResult, builder);
        }
    }

    RequestParser[] getParsers() {
        return parsers;
    }

}
