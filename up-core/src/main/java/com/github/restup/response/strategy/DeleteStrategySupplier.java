package com.github.restup.response.strategy;

import com.github.restup.annotations.model.DeleteStrategy;
import com.github.restup.registry.Resource;
import java.io.Serializable;

public interface DeleteStrategySupplier {

    <T, ID extends Serializable> DeleteStrategy getStrategy(Resource<T, ID> resource);

}
