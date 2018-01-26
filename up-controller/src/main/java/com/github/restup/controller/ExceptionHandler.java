package com.github.restup.controller;

import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerResponse;

/**
 * Handles all Exceptions thrown when handling a request in {@link ResourceController#request(com.github.restup.controller.model.ResourceControllerRequest, ResourceControllerResponse)}
 * @author abuttaro
 *
 */
public interface ExceptionHandler {

    Object handleException(ResourceControllerRequest request, ResourceControllerResponse response, Throwable e);

}
