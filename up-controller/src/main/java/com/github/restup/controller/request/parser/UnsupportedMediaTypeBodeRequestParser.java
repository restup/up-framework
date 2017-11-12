package com.github.restup.controller.request.parser;

import com.github.restup.controller.content.negotiation.ContentTypeNegotiation;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.errors.ErrorBuilder;

/**
 * Simply throws an unsupported media type error.
 * May be added to {@link RequestParserChain} as last item to handle request if
 * none others do (default behavior)
 */
public class UnsupportedMediaTypeBodeRequestParser implements RequestParser {
    public void parse(ResourceControllerRequest request, ParsedResourceControllerRequest.Builder<?> builder) {
        if (request.getMethod().requiresData()) {
            throw ErrorBuilder.builder(request.getResource())
                    .status(ErrorBuilder.ErrorCodeStatus.UNSUPPORTED_MEDIA_TYPE)
                    .meta(ContentTypeNegotiation.CONTENT_TYPE, request.getContentType())
                    .buildException();
        }
    }
}
