package com.github.restup.service.model.request;

import com.github.restup.bind.param.ParameterProvider;
import com.github.restup.path.ResourcePath;
import com.github.restup.query.ResourceQueryStatement;
import com.github.restup.registry.Resource;

import java.io.Serializable;
import java.util.List;

/**
 * Default {@link RequestObjectFactory} implementation returning default, basic request
 * object implementations.
 */
public class DefaultRequestObjectFactory implements RequestObjectFactory {

    public <T> CreateRequest<T> getCreateRequest(Resource<T, ?> requestedResource, T data, List<ResourcePath> requestedPaths, List<ResourceQueryStatement> requestedQueries, ParameterProvider parameterProvider) {
        return new BasicCreateRequest<T>(requestedResource, data, requestedPaths, requestedQueries, parameterProvider);
    }

    public <T, ID extends Serializable> ReadRequest<T, ID> getReadRequest(Resource<T, ID> requestedResource, ID id, List<ResourceQueryStatement> requestedQueries, ParameterProvider parameterProvider) {
        return new BasicReadRequest<T, ID>(requestedResource, id, requestedQueries, parameterProvider);
    }

    public <T, ID extends Serializable> ListRequest<T> getListRequest(Resource<T, ID> requestedResource, List<ResourceQueryStatement> requestedQueries, ParameterProvider parameterProvider) {
        return new BasicListRequest<T>(requestedResource, requestedQueries, parameterProvider);
    }

    public <T, ID extends Serializable> UpdateRequest<T, ID> getUpdateRequest(Resource<T, ID> requestedResource, ID id, T data, List<ResourcePath> requestedPaths, List<ResourceQueryStatement> requestedQueries, ParameterProvider parameterProvider) {
        return new BasicUpdateRequest<T, ID>(requestedResource, id, data, requestedPaths, requestedQueries, parameterProvider);
    }

    public <T, ID extends Serializable> DeleteRequest<T, ID> getDeleteRequest(Resource<T, ID> requestedResource, ID id, List<ResourceQueryStatement> requestedQueries, ParameterProvider parameterProvider) {
        return new BasicDeleteRequest<T, ID>(requestedResource, id, requestedQueries, parameterProvider);
    }

}
