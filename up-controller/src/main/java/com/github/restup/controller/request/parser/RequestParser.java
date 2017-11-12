package com.github.restup.controller.request.parser;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;

/**
 * Parses {@link ResourceControllerRequest}
 */
public interface RequestParser {

    /**
     * parse request appending results to builder
     *
     * @param request
     * @param builder
     */
    void parse(ResourceControllerRequest request, ParsedResourceControllerRequest.Builder<?> builder);

}
