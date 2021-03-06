package com.github.restup.controller.model;

import static com.github.restup.util.UpUtils.unmodifiableList;

import com.github.restup.path.ResourcePath;
import com.github.restup.query.ResourceQueryStatement;
import com.github.restup.service.model.ResourceData;
import java.util.Enumeration;
import java.util.List;

/**
 * Contains result of parsing parameters
 */
class BasicParsedResourceControllerRequest<T> extends BasicResourceControllerRequest implements ParsedResourceControllerRequest<T> {

    private final T data;
    private final List<ResourcePath> requestedPaths;
    private final List<ResourceQueryStatement> requestedQueries;
    private final ResourceControllerRequest request;
    private final List<String> acceptedParameterNames;
    private final List<String> acceptedResourceParameterNames;
    private final String pageLimitParameterName;
    private final String pageOffsetParameterName;
    private final boolean pageOffsetOneBased;

    BasicParsedResourceControllerRequest(T data, List<ResourcePath> requestedPaths,
        List<ResourceQueryStatement> requestedQueries, ResourceControllerRequest request,
        ResourceData<?> body, List<String> acceptedParameterNames,
        List<String> acceptedResourceParameterNames, String pageLimitParameterName,
        String pageOffsetParameterName, boolean pageOffsetOneBased) {
        super(request.getMethod(), request.getResource(), request.getIds(), request.getRelationship(), request.getResourceRelationship(), body, request.getContentType(), request.getBaseRequestUrl(), request.getRequestUrl());
        this.data = data;
        this.requestedPaths = unmodifiableList(requestedPaths);
        this.requestedQueries = unmodifiableList(requestedQueries);
        this.request = request;
        this.acceptedParameterNames = acceptedParameterNames;
        this.acceptedResourceParameterNames = acceptedResourceParameterNames;
        this.pageLimitParameterName = pageLimitParameterName;
        this.pageOffsetParameterName = pageOffsetParameterName;
        this.pageOffsetOneBased = pageOffsetOneBased;
    }

    @Override
    public T getData() {
        return data;
    }

    @Override
    public List<ResourcePath> getRequestedPaths() {
        return requestedPaths;
    }

    @Override
    public List<ResourceQueryStatement> getRequestedQueries() {
        return requestedQueries;
    }

    @Override
    public List<String> getParameterNames() {
        return request.getParameterNames();
    }

    @Override
    public String[] getParameter(String parameterName) {
        return request.getParameter(parameterName);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return request == null ? null : request.getHeaders(name);
    }

    @Override
    public String getPageLimitParameterName() {
        return pageLimitParameterName;
    }

    @Override
    public String getPageOffsetParameterName() {
        return pageOffsetParameterName;
    }

    @Override
    public List<String> getAcceptedResourceParameterNames() {
        return acceptedResourceParameterNames;
    }

    @Override
    public List<String> getAcceptedParameterNames() {
        return acceptedParameterNames;
    }

    @Override
    public boolean isPageOffsetOneBased() {
        return pageOffsetOneBased;
    }
    
    public ResourceControllerRequest getRequest() {
        return request;
    }

}
