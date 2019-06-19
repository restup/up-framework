package com.github.restup.response.strategy;

import com.github.restup.annotations.model.CreateStrategy;
import com.github.restup.registry.Resource;
import java.io.Serializable;
import java.util.Objects;

public class DefaultCreateStrategySupplier implements CreateStrategySupplier {

    private final CreateStrategy defaultType;

    public DefaultCreateStrategySupplier(CreateStrategy defaultType) {
        this.defaultType = defaultType;
    }

    public DefaultCreateStrategySupplier() {
        this(CreateStrategy.CREATED);
    }

    @Override
    public <T, ID extends Serializable> CreateStrategy getStrategy(Resource<T, ID> resource,
        ID id) {

        CreateStrategy type = resource
            .getCreateStrategy(); //TODO default behavior from resource
        if (Objects.equals(CreateStrategy.DEFAULT, type)) {
            type = defaultType;
        }
        if (Objects.equals(CreateStrategy.DEFAULT, type)) {
            type = id == null ? CreateStrategy.CREATED : CreateStrategy.NO_CONTENT;
        }
        return type;

    }
}
