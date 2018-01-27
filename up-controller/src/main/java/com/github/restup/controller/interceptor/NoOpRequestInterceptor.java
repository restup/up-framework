package com.github.restup.controller.interceptor;

import com.github.restup.controller.model.ParsedResourceControllerRequest;

/**
 * A no op implementation of {@link RequestInterceptor}
 */
public class NoOpRequestInterceptor implements RequestInterceptor {

    @Override
    public <T> void before(ParsedResourceControllerRequest<T> request) {
        // NOOP
    }

    @Override
    public <T> void after(ParsedResourceControllerRequest<T> request) {
        // NOOP
    }

}
