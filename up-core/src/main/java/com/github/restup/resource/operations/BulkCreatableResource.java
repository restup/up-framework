package com.github.restup.resource.operations;

import com.github.restup.annotations.operations.BulkCreateResource;
import com.github.restup.service.model.request.BulkRequest;
import com.github.restup.service.model.request.CreateRequest;
import com.github.restup.service.model.response.PersistenceResult;
import java.util.List;

public interface BulkCreatableResource<T> {

    @BulkCreateResource
    PersistenceResult<List<PersistenceResult<T>>> create(BulkRequest<CreateRequest<T>> request);

}
