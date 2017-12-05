package com.github.restup.service.model.request;

import static com.github.restup.util.UpUtils.unmodifiableList;

import com.github.restup.bind.param.ParameterProvider;
import com.github.restup.query.ResourceQueryStatement;
import com.github.restup.registry.Resource;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRequest implements QueryRequest, ParameterProvider {

    private final ParameterProvider delegate;
    private final Resource<?, ?> requestedResource;
    private final List<ResourceQueryStatement> requestedQueries;
    private final ResourceQueryStatement query;

    public AbstractRequest(Resource<?, ?> requestedResource, List<ResourceQueryStatement> requestedQueries, ParameterProvider parameterProvider) {
        super();
        this.requestedResource = requestedResource;
        this.query = ResourceQueryStatement.getQuery(requestedResource, requestedQueries);
        List<ResourceQueryStatement> related = null;
        if (requestedQueries != null) {
            related = new ArrayList<ResourceQueryStatement>(requestedQueries);
            if (query != null) {
                related.remove(query);
            }
        }
        this.requestedQueries = unmodifiableList(related);
        this.delegate = parameterProvider;
    }

    public List<ResourceQueryStatement> getSecondaryQueries() {
        return requestedQueries;
    }

    public ResourceQueryStatement getQuery() {
        return query;
    }

    public List<String> getParameterNames() {
        return delegate.getParameterNames();
    }

    public String[] getParameter(String parameterName) {
        return delegate.getParameter(parameterName);
    }

    public Resource<?, ?> getResource() {
        return requestedResource;
    }

    public ParameterProvider getDelegate() {
        return delegate;
    }
}
