package com.github.restup.controller.request.parser;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;

public class NoOpRequestParser implements RequestParser {
    public void parse(ResourceControllerRequest request, ParsedResourceControllerRequest.Builder<?> builder) {

    }
}
