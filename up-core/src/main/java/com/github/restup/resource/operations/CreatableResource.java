package com.github.restup.resource.operations;

import com.github.restup.annotations.operations.CreateResource;
import com.github.restup.service.model.request.CreateRequest;
import com.github.restup.service.model.response.PersistenceResult;

public interface CreatableResource<T> {

    @CreateResource
    PersistenceResult<T> create(CreateRequest<T> request);

}
