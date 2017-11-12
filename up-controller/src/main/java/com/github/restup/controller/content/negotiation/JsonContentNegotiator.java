package com.github.restup.controller.content.negotiation;

import com.github.restup.controller.model.MediaType;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerResponse;
import com.github.restup.controller.model.result.JsonResult;

public class JsonContentNegotiator extends ContentTypeNegotiation {

    public JsonContentNegotiator() {
        super(MediaType.APPLICATION_JSON);
    }

    @Override
    Object format(ParsedResourceControllerRequest<?> request, ResourceControllerResponse response, Object result) {
        return new JsonResult(request, result);
    }

}
