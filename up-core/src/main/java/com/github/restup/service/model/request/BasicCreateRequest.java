package com.github.restup.service.model.request;

import com.github.restup.bind.param.ParameterProvider;
import com.github.restup.path.ResourcePath;
import com.github.restup.query.ResourceQueryStatement;
import com.github.restup.registry.Resource;
import java.util.List;

/**
 * Request object for create operations.
 *
 * @author abuttaro
 */
public class BasicCreateRequest<T> extends AbstractPersistenceRequest<T> implements CreateRequest<T> {

    public BasicCreateRequest(Resource<T, ?> requestedResource, T data, List<ResourcePath> requestedPaths, List<ResourceQueryStatement> requestedQueries, ParameterProvider parameterProvider) {
        super(requestedResource, data, requestedPaths, requestedQueries, parameterProvider);
    }

}
