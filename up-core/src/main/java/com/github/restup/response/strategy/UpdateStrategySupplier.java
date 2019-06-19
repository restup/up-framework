package com.github.restup.response.strategy;

import com.github.restup.annotations.model.UpdateStrategy;
import com.github.restup.registry.Resource;
import java.io.Serializable;

public interface UpdateStrategySupplier {

    <T, ID extends Serializable> UpdateStrategy getStrategy(Resource<T, ID> resource);

}
