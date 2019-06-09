package com.github.restup.controller.model;

import com.github.restup.service.model.ResourceData;

/**
 * In an http request, this is a partially parsed details from the request, having parsed the request path to obtain resource info and ids.
 */
public abstract class BasicResourceControllerRequest implements ResourceControllerRequest {

    private final HttpMethod method;
    private final ResourceData<?> body;
    private final String contentType;
    private final String baseRequestUrl;
    private final String requestUrl;

    protected BasicResourceControllerRequest(HttpMethod method, ResourceData<?> body
            , String contentType, String baseRequestUrl, String requestUrl) {
        this.method = method;
        this.body = body;
        this.contentType = contentType;
        this.baseRequestUrl = baseRequestUrl;
        this.requestUrl = requestUrl;
    }

    @Override
    public Iterable<String> getHeaders(String name) {
        return null;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public ResourceData<?> getBody() {
        return body;
    }

    @Override
    public String getBaseRequestUrl() {
        return baseRequestUrl;
    }

    @Override
    public String getRequestUrl() {
        return requestUrl;
    }

}
