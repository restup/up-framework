package com.github.restup.response.strategy;

import com.github.restup.annotations.model.CreateStrategy;
import com.github.restup.registry.Resource;
import java.io.Serializable;

public interface CreateStrategySupplier {

    <T, ID extends Serializable> CreateStrategy getStrategy(Resource<T, ID> resource, ID id);


}
