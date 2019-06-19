package com.github.restup.service.model.request;

import com.github.restup.annotations.model.CreateStrategy;
import com.github.restup.annotations.model.DeleteStrategy;
import com.github.restup.annotations.model.UpdateStrategy;
import com.github.restup.bind.param.ParameterProvider;
import com.github.restup.path.ResourcePath;
import com.github.restup.query.ResourceQueryStatement;
import com.github.restup.registry.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * Default {@link RequestObjectFactory} implementation returning default, basic request object implementations.
 */
public class DefaultRequestObjectFactory implements RequestObjectFactory {

    @Override
    public <T> CreateRequest<T> getCreateRequest(Resource<T, ?> requestedResource, T data,
        List<ResourcePath> requestedPaths, List<ResourceQueryStatement> requestedQueries,
        ParameterProvider parameterProvider, CreateStrategy createStrategy) {
        return new BasicCreateRequest<>(requestedResource, data, requestedPaths, requestedQueries,
            parameterProvider, createStrategy);
    }

    @Override
    public <T, ID extends Serializable> ReadRequest<T, ID> getReadRequest(Resource<T, ID> requestedResource, ID id, List<ResourceQueryStatement> requestedQueries, ParameterProvider parameterProvider) {
        return new BasicReadRequest<>(requestedResource, id, requestedQueries, parameterProvider);
    }

    @Override
    public <T, ID extends Serializable> ListRequest<T> getListRequest(Resource<T, ID> requestedResource, List<ResourceQueryStatement> requestedQueries, ParameterProvider parameterProvider) {
        return new BasicListRequest<>(requestedResource, requestedQueries, parameterProvider);
    }

    @Override
    public <T, ID extends Serializable> UpdateRequest<T, ID> getUpdateRequest(
        Resource<T, ID> requestedResource, ID id, T data, List<ResourcePath> requestedPaths,
        List<ResourceQueryStatement> requestedQueries, ParameterProvider parameterProvider,
        UpdateStrategy updateStrategy) {
        return new BasicUpdateRequest<>(requestedResource, id, data, requestedPaths,
            requestedQueries, parameterProvider, updateStrategy);
    }

    @Override
    public <T, ID extends Serializable> DeleteRequest<T, ID> getDeleteRequest(
        Resource<T, ID> requestedResource, ID id, List<ResourceQueryStatement> requestedQueries,
        ParameterProvider parameterProvider, DeleteStrategy deleteStrategy) {
        return new BasicDeleteRequest<>(requestedResource, id, requestedQueries, parameterProvider,
            deleteStrategy);
    }

}
