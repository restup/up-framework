package com.github.restup.controller;

import com.github.restup.controller.model.ResourceControllerResponse;

public interface ExceptionHandler {

    Object handleException(ResourceControllerResponse response, Throwable e);

}
