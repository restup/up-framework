package com.github.restup.response.strategy;

import com.github.restup.annotations.model.DeleteStrategy;
import com.github.restup.registry.Resource;
import java.io.Serializable;
import java.util.Objects;

public class DefaultDeleteStrategySupplier implements DeleteStrategySupplier {

    private final DeleteStrategy defaultType;

    public DefaultDeleteStrategySupplier(DeleteStrategy defaultType) {
        this.defaultType = defaultType;
    }

    public DefaultDeleteStrategySupplier() {
        this(DeleteStrategy.NO_CONTENT);
    }

    @Override
    public <T, ID extends Serializable> DeleteStrategy getStrategy(Resource<T, ID> resource) {
        DeleteStrategy type = resource.getDeleteStrategy();
        if (Objects.equals(type, DeleteStrategy.DEFAULT)) {
            type = defaultType;
        }
        return type;
    }
}
