package com.github.restup.controller.model.result;

import com.github.restup.controller.model.ParsedResourceControllerRequest;

public class JsonResult extends NegotiatedResult {

    public JsonResult(ParsedResourceControllerRequest<?> request, Object result) {
        super(request, result);
    }

}
