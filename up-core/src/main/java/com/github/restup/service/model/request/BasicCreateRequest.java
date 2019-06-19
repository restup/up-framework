package com.github.restup.service.model.request;

import com.github.restup.annotations.model.CreateStrategy;
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

    private final CreateStrategy createStrategy;

    public BasicCreateRequest(Resource<T, ?> requestedResource, T data,
        List<ResourcePath> requestedPaths, List<ResourceQueryStatement> requestedQueries,
        ParameterProvider parameterProvider, CreateStrategy createStrategy) {
        super(requestedResource, data, requestedPaths, requestedQueries, parameterProvider);
        this.createStrategy = createStrategy;
    }

    @Override
    public CreateStrategy getCreateStrategy() {
        return createStrategy;
    }

}
