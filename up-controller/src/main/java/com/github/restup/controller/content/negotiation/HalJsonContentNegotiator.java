package com.github.restup.controller.content.negotiation;

import com.github.restup.controller.model.MediaType;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerResponse;
import com.github.restup.controller.model.result.HalJsonResult;

public class HalJsonContentNegotiator extends ContentTypeNegotiation {

    public HalJsonContentNegotiator() {
        super(MediaType.APPLICATION_JSON_HAL);
    }

    @Override
    Object format(ParsedResourceControllerRequest<?> request, ResourceControllerResponse response, Object result) {
        return new HalJsonResult(request, result);
    }


}
