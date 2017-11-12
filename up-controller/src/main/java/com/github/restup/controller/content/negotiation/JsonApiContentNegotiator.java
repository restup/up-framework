package com.github.restup.controller.content.negotiation;

import com.github.restup.controller.linking.LinkBuilder;
import com.github.restup.controller.linking.LinkBuilderFactory;
import com.github.restup.controller.model.MediaType;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.result.JsonApiResult;
import com.github.restup.controller.model.ResourceControllerResponse;

/**
 * Content negotiation for JSON API results.
 */
public class JsonApiContentNegotiator extends ContentTypeNegotiation {

    private final LinkBuilderFactory factory;

    public JsonApiContentNegotiator(LinkBuilderFactory factory) {
        super(MediaType.APPLICATION_JSON_API);
        this.factory = factory;
    }

    @Override
    public Object format(ParsedResourceControllerRequest<?> request, ResourceControllerResponse response, Object result) {
        LinkBuilder linkBuilder = factory.getLinkBuilder(request, result);
        return new JsonApiResult(linkBuilder, request, result);
    }


}
