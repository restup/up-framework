package com.github.restup.controller.interceptor;

import com.github.restup.controller.model.ParsedResourceControllerRequest;

/**
 * A no op implementation of {@link RequestInterceptor}
 */
public class NoOpRequestInterceptor implements RequestInterceptor {

    public <T> void before(ParsedResourceControllerRequest<T> request) {
        // NOOP
    }

    public <T> void after(ParsedResourceControllerRequest<T> request) {
        // NOOP
    }

}
