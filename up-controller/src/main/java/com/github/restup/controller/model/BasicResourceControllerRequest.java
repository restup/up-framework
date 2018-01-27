package com.github.restup.controller.model;

import java.util.Enumeration;
import java.util.List;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRelationship;
import com.github.restup.service.model.ResourceData;

/**
 * In an http request, this is a partially parsed details from the request, having parsed the request path to obtain resource info and ids.
 */
public abstract class BasicResourceControllerRequest implements ResourceControllerRequest {

    private final HttpMethod method;
    private final Resource<?, ?> resource;
    private final Resource<?, ?> relationship;
    private final ResourceRelationship<?, ?, ?, ?> resourceRelationship;
    private final ResourceData<?> body;
    private final List<?> ids;
    private final String contentType;
    private final String baseRequestUrl;
    private final String requestUrl;

    protected BasicResourceControllerRequest(HttpMethod method, Resource<?, ?> resource, List<?> ids, Resource<?, ?> relationship, ResourceRelationship<?, ?, ?, ?> resourceRelationship, ResourceData<?> body
            , String contentType, String baseRequestUrl, String requestUrl) {
        this.resource = resource;
        this.ids = ids;
        this.relationship = relationship;
        this.resourceRelationship = resourceRelationship;
        this.method = method;
        this.body = body;
        this.contentType = contentType;
        this.baseRequestUrl = baseRequestUrl;
        this.requestUrl = requestUrl;
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
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
    public Resource<?, ?> getResource() {
        return resource;
    }

    @Override
    public Resource<?, ?> getRelationship() {
        return relationship;
    }

    @Override
    public ResourceRelationship<?, ?, ?, ?> getResourceRelationship() {
        return resourceRelationship;
    }

    @Override
    public List<?> getIds() {
        return ids;
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
