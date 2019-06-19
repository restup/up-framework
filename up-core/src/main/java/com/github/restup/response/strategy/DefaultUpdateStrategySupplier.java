package com.github.restup.response.strategy;

import com.github.restup.annotations.model.UpdateStrategy;
import com.github.restup.registry.Resource;
import java.io.Serializable;
import java.util.Objects;

public class DefaultUpdateStrategySupplier implements UpdateStrategySupplier {

    private final UpdateStrategy defaultType;

    public DefaultUpdateStrategySupplier(UpdateStrategy defaultType) {
        this.defaultType = defaultType;
    }

    public DefaultUpdateStrategySupplier() {
        this(UpdateStrategy.UPDATED);
    }

    @Override
    public <T, ID extends Serializable> UpdateStrategy getStrategy(Resource<T, ID> resource) {
        UpdateStrategy type = resource.getUpdateStrategy();
        if (Objects.equals(type, UpdateStrategy.DEFAULT)) {
            type = defaultType;
        }
        return type;
    }
}
