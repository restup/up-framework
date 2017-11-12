package com.github.restup.resource.operations;

import com.github.restup.annotations.operations.BulkCreateResource;
import com.github.restup.service.model.request.BulkRequest;
import com.github.restup.service.model.request.CreateRequest;
import com.github.restup.service.model.response.PersistenceResult;

import java.io.Serializable;
import java.util.List;

public interface BulkCreatableResource<T, ID extends Serializable> {

    @BulkCreateResource
    public PersistenceResult<List<PersistenceResult<T>>> create(BulkRequest<CreateRequest<T>> request);

}
