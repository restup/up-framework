package com.github.restup.controller.interceptor;

import com.github.restup.controller.model.ParsedResourceControllerRequest;

/**
 * Offers a means to wrap method execution following parsing and validation of the request.
 */
public interface RequestInterceptor {

    /**
     * called after request is parsed and validated and before method is executed
     *
     * @param request intercepted
     * @param <T> of resource
     */
    <T> void before(ParsedResourceControllerRequest<T> request);

    /**
     * called immediately after method is executed, prior to content negotiation
     *
     * @param request intercepted
     * @param <T> of resource
     */
    <T> void after(ParsedResourceControllerRequest<T> request);

}
