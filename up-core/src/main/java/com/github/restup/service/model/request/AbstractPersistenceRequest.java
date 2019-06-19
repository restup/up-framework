package com.github.restup.service.model.request;

import static com.github.restup.util.UpUtils.unmodifiableList;

import com.github.restup.bind.param.ParameterProvider;
import com.github.restup.path.ResourcePath;
import com.github.restup.query.ResourceQueryStatement;
import com.github.restup.registry.Resource;
import java.util.List;

public class AbstractPersistenceRequest<T> extends AbstractRequest implements PersistenceRequest<T> {

    private final T data;
    private final List<ResourcePath> requestedPaths;

    public AbstractPersistenceRequest(Resource<T, ?> requestedResource, T data,
        List<ResourcePath> requestedPaths, List<ResourceQueryStatement> requestedQueries,
        ParameterProvider parameterProvider) {
        super(requestedResource, requestedQueries, parameterProvider);
        this.data = data;
        this.requestedPaths = unmodifiableList(requestedPaths);
    }

    @Override
    public T getData() {
        return data;
    }

    /**
     * The requested paths. Never null and immutable
     */
    @Override
    public List<ResourcePath> getRequestedPaths() {
        return requestedPaths;
    }

    @Override
    public boolean hasPath(ResourcePath other) {
        return ResourcePath.hasPath(requestedPaths, other);
    }

}
