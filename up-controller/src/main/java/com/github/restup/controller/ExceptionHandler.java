package com.github.restup.controller;

import com.github.restup.controller.model.AbstractResourceControllerRequestBuilder;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerResponse;

/**
 * Handles all Exceptions thrown when handling a request in {@link ResourceController#request(AbstractResourceControllerRequestBuilder, ResourceControllerResponse)}
 * @author abuttaro
 *
 */
public interface ExceptionHandler {

    static ExceptionHandler getDefaultInstance() {
        return DefaultExceptionHandler.getInstance();
    }

    Object handleException(ResourceControllerRequest request, ResourceControllerResponse response,
        Throwable e);

}
