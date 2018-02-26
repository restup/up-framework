package com.github.restup.controller.content.negotiation;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerResponse;

public class ContentNegotiatorChain implements ContentNegotiator {

    private final ContentNegotiator[] contentNegotiators;

    public ContentNegotiatorChain(ContentNegotiator... contentNegotiators) {
        this.contentNegotiators = contentNegotiators;
    }

    @Override
    public <T> boolean accept(ResourceControllerRequest request) {
        return null != getContentNegotiator(request);
    }

    @Override
    public <T> Object formatResponse(ParsedResourceControllerRequest<T> request, ResourceControllerResponse response, Object result) {
        return getContentNegotiator(request).formatResponse(request, response, result);
    }

    /**
     * @return ContentNegotiator for content type passed
     * @throws RequestErrorException if content type is not supported
     */
    private ContentNegotiator getContentNegotiator(ResourceControllerRequest request) {
        for (ContentNegotiator contentNegotiator : contentNegotiators) {
            if (contentNegotiator.accept(request)) {
                return contentNegotiator;
            }
        }
        return null;
    }

}
