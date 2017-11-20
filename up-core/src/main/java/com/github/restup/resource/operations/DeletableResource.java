package com.github.restup.resource.operations;

import com.github.restup.annotations.operations.DeleteResource;
import com.github.restup.service.model.request.DeleteRequest;
import com.github.restup.service.model.response.PersistenceResult;

import java.io.Serializable;

public interface DeletableResource<T, ID extends Serializable> {

    @DeleteResource
    PersistenceResult<T> delete(DeleteRequest<T, ID> request);
}
