package com.github.restup.resource.operations;

import com.github.restup.annotations.operations.BulkDeleteResource;
import com.github.restup.service.model.request.BulkRequest;
import com.github.restup.service.model.request.DeleteRequest;
import com.github.restup.service.model.response.PersistenceResult;
import java.io.Serializable;
import java.util.List;

public interface BulkDeletableResource<T, ID extends Serializable> {

    @BulkDeleteResource
    PersistenceResult<List<PersistenceResult<T>>> delete(BulkRequest<DeleteRequest<T, ID>> request);

}
