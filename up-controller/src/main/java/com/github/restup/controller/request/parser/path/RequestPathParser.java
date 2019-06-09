package com.github.restup.controller.request.parser.path;

import com.github.restup.controller.model.ResourceControllerRequest;

public interface RequestPathParser {

    RequestPathParserResult parsePath(ResourceControllerRequest request);
}
