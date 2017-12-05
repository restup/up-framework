package com.github.restup.controller.interceptor;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.util.Assert;

/**
 * Iterates over an array of {@link RequestInterceptor}s for {@link #before(ParsedResourceControllerRequest)} and {@link #after(ParsedResourceControllerRequest)}
 *
 * @author abuttaro
 */
public class RequestInterceptorChain implements RequestInterceptor {

    private final RequestInterceptor[] interceptors;

    public RequestInterceptorChain(RequestInterceptor... interceptors) {
        super();
        Assert.notEmpty("interceptors are required", interceptors);
        this.interceptors = interceptors;
    }

    public <T> void before(ParsedResourceControllerRequest<T> request) {
        for (RequestInterceptor interceptor : interceptors) {
            interceptor.before(request);
        }
    }

    public <T> void after(ParsedResourceControllerRequest<T> request) {
        for (RequestInterceptor interceptor : interceptors) {
            interceptor.after(request);
        }
    }

}
