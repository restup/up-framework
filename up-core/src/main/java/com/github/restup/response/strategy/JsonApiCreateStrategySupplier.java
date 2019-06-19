package com.github.restup.response.strategy;

import com.github.restup.annotations.model.CreateStrategy;
import com.github.restup.registry.Resource;
import java.io.Serializable;

public class JsonApiCreateStrategySupplier implements CreateStrategySupplier {

    private final CreateStrategySupplier delegate;

    public JsonApiCreateStrategySupplier(CreateStrategySupplier delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T, ID extends Serializable> CreateStrategy getStrategy(Resource<T, ID> resource,
        ID id) {
        CreateStrategy type = delegate.getStrategy(resource, id);
        if (!CreateStrategy.ACCEPTED.equals(type)) {
            // for json api if id is not client generated and response is not Accepted
            // then it must be created
            if (id == null) {
                type = CreateStrategy.CREATED;
            }
        }
        return type;
    }
}
