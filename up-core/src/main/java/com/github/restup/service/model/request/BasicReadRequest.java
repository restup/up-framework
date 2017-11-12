package com.github.restup.service.model.request;

import com.github.restup.bind.param.ParameterProvider;
import com.github.restup.query.ResourceQueryStatement;
import com.github.restup.registry.Resource;

import java.io.Serializable;
import java.util.List;

/**
 * A request for read access by id.
 *
 * @param <T>
 * @param <ID>
 * @author abuttaro
 */
public class BasicReadRequest<T, ID extends Serializable> extends AbstractRequest implements ReadRequest<T, ID> {

    private final ID id;

    public BasicReadRequest(Resource<T, ID> requestedResource, ID id, List<ResourceQueryStatement> requestedQueries, ParameterProvider parameterProvider) {
        super(requestedResource, requestedQueries, parameterProvider);
        this.id = id;
    }

    public ID getId() {
        return id;
    }

}
