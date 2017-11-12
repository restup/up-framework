package com.github.restup.resource.operations;

import com.github.restup.annotations.operations.CreateResource;
import com.github.restup.service.model.request.CreateRequest;
import com.github.restup.service.model.response.PersistenceResult;

import java.io.Serializable;

public interface CreatableResource<T, ID extends Serializable> {

    @CreateResource
    public PersistenceResult<T> create(CreateRequest<T> request);

}
