package com.github.restup.resource.operations;

import com.github.restup.annotations.operations.BulkUpdateResource;
import com.github.restup.service.model.request.BulkRequest;
import com.github.restup.service.model.request.UpdateRequest;
import com.github.restup.service.model.response.PersistenceResult;
import java.io.Serializable;
import java.util.List;

public interface BulkUpdatableResource<T, ID extends Serializable> {

    @BulkUpdateResource
    PersistenceResult<List<PersistenceResult<T>>> update(BulkRequest<UpdateRequest<T, ID>> request);

}
