package com.github.restup.service.model.request;

import com.github.restup.annotations.model.UpdateStrategy;
import com.github.restup.bind.param.ParameterProvider;
import com.github.restup.path.ResourcePath;
import com.github.restup.query.ResourceQueryStatement;
import com.github.restup.registry.Resource;
import java.io.Serializable;
import java.util.List;

public class BasicUpdateRequest<T, ID extends Serializable> extends AbstractPersistenceRequest<T> implements UpdateRequest<T, ID> {

    private final ID id;
    private final UpdateStrategy updateStrategy;

    public BasicUpdateRequest(Resource<T, ID> requestedResource, ID id, T data,
        List<ResourcePath> requestedPaths, List<ResourceQueryStatement> requestedQueries,
        ParameterProvider parameterProvider, UpdateStrategy updateStrategy) {
        super(requestedResource, data, requestedPaths, requestedQueries, parameterProvider);
        this.id = id;
        this.updateStrategy = updateStrategy;
    }

    @Override
    public ID getId() {
        return id;
    }

    @Override
    public UpdateStrategy getUpdateStrategy() {
        return updateStrategy;
    }
}
