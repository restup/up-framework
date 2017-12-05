package com.github.restup.service.model.request;

import com.github.restup.bind.param.ParameterProvider;
import com.github.restup.query.ResourceQueryStatement;
import com.github.restup.registry.Resource;
import java.util.List;

public class BasicListRequest<T> extends AbstractRequest implements ListRequest<T> {

    public BasicListRequest(Resource<T, ?> requestedResource, List<ResourceQueryStatement> requestedQueries, ParameterProvider parameterProvider) {
        super(requestedResource, requestedQueries, parameterProvider);
    }

}
