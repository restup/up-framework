package com.github.restup.controller.request.parser;

import com.github.restup.controller.content.negotiation.ContentTypeNegotiation;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.errors.RequestError;
import com.github.restup.errors.StatusCode;

/**
 * Simply throws an unsupported media type error. May be added to {@link RequestParserChain} as last
 * item to handle request if none others do (default behavior)
 */
public class UnsupportedMediaTypeBodyRequestParser implements RequestParser {

    @Override
    public void parse(ResourceControllerRequest request, ParsedResourceControllerRequest.Builder<?> builder) {
        throw RequestError.builder(request.getResource())
                .status(StatusCode.UNSUPPORTED_MEDIA_TYPE)
                .meta(ContentTypeNegotiation.CONTENT_TYPE, request.getContentType())
                .buildException();
    }
}
