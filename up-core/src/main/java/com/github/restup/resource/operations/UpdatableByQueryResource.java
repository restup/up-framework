package com.github.restup.resource.operations;

import com.github.restup.annotations.operations.UpdateResourceByQuery;
import com.github.restup.service.model.request.UpdateRequest;
import com.github.restup.service.model.response.PersistenceResult;
import java.io.Serializable;
import java.util.List;

public interface UpdatableByQueryResource<T, ID extends Serializable> {

    @UpdateResourceByQuery
    PersistenceResult<List<PersistenceResult<T>>> updateByQueryCriteria(UpdateRequest<T, ID> request);
}
