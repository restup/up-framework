package com.github.restup.resource.operations;

import com.github.restup.annotations.operations.UpdateResource;
import com.github.restup.service.model.request.UpdateRequest;
import com.github.restup.service.model.response.PersistenceResult;
import java.io.Serializable;

public interface UpdatableResource<T, ID extends Serializable> {

    @UpdateResource
    PersistenceResult<T> update(UpdateRequest<T, ID> request);
}
