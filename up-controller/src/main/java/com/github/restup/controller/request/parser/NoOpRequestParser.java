package com.github.restup.controller.request.parser;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.request.parser.path.RequestPathParserResult;

public class NoOpRequestParser implements RequestParser {

    @Override
    public void parse(ResourceControllerRequest request,
        RequestPathParserResult requestPathParserResult,
        ParsedResourceControllerRequest.Builder<?> builder) {

    }
}
