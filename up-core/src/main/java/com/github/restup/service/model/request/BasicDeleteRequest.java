package com.github.restup.service.model.request;

import com.github.restup.annotations.model.DeleteStrategy;
import com.github.restup.bind.param.ParameterProvider;
import com.github.restup.query.ResourceQueryStatement;
import com.github.restup.registry.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * A request to delete a resource by id.
 *
 * @author abuttaro
 */
public class BasicDeleteRequest<T, ID extends Serializable> extends AbstractRequest implements DeleteRequest<T, ID> {

    private final ID id;
    private final DeleteStrategy deleteStrategy;

    public BasicDeleteRequest(Resource<T, ID> requestedResource, ID id,
        List<ResourceQueryStatement> requestedQueries, ParameterProvider parameterProvider,
        DeleteStrategy deleteStrategy) {
        super(requestedResource, requestedQueries, parameterProvider);
        this.id = id;
        this.deleteStrategy = deleteStrategy;
    }

    @Override
    public ID getId() {
        return id;
    }

    @Override
    public DeleteStrategy getDeleteStrategy() {
        return deleteStrategy;
    }
}
