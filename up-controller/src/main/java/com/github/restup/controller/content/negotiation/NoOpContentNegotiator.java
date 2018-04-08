package com.github.restup.controller.content.negotiation;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerResponse;

/**
 * {@link ContentNegotiator} with No Op implementations.
 */
public class NoOpContentNegotiator implements ContentNegotiator {

    @Override
    public boolean accept(ResourceControllerRequest request) {
        return request != null;
    }

    @Override
    public <T> Object formatResponse(ParsedResourceControllerRequest<T> request, ResourceControllerResponse response, Object result) {
        return result;
    }

}
