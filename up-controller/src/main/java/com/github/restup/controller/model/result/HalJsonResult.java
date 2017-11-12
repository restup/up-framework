package com.github.restup.controller.model.result;

import com.github.restup.controller.model.ParsedResourceControllerRequest;

public class HalJsonResult extends NegotiatedResult {

    public HalJsonResult(ParsedResourceControllerRequest<?> request, Object result) {
        super(request, result);
    }

}
