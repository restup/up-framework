package com.github.restup.service.model.request;

import com.github.restup.bind.param.ParameterProvider;
import com.github.restup.path.ResourcePath;
import com.github.restup.query.ResourceQueryStatement;
import com.github.restup.registry.Resource;

import java.io.Serializable;
import java.util.List;

/**
 * A factory for creating request object implementations, allowing for customization
 * of the request objects instantiated for and used when handling requests
 */
public interface RequestObjectFactory {

    //TODO docs

    <T> CreateRequest<T> getCreateRequest(Resource<T, ?> requestedResource, T data, List<ResourcePath> requestedPaths, List<ResourceQueryStatement> requestedQueries, ParameterProvider parameterProvider);

    <T, ID extends Serializable> ReadRequest<T, ID> getReadRequest(Resource<T, ID> requestedResource, ID id, List<ResourceQueryStatement> requestedQueries, ParameterProvider parameterProvider);

    <T, ID extends Serializable> ListRequest<T> getListRequest(Resource<T, ID> requestedResource, List<ResourceQueryStatement> requestedQueries, ParameterProvider parameterProvider);

    <T, ID extends Serializable> UpdateRequest<T, ID> getUpdateRequest(Resource<T, ID> requestedResource, ID id, T data, List<ResourcePath> requestedPaths, List<ResourceQueryStatement> requestedQueries, ParameterProvider parameterProvider);

    <T, ID extends Serializable> DeleteRequest<T, ID> getDeleteRequest(Resource<T, ID> requestedResource, ID id, List<ResourceQueryStatement> requestedQueries, ParameterProvider parameterProvider);

}
