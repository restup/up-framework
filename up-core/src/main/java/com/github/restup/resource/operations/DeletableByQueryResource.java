package com.github.restup.resource.operations;

import com.github.restup.annotations.operations.DeleteResourceByQuery;
import com.github.restup.service.model.request.DeleteRequest;
import com.github.restup.service.model.response.PersistenceResult;
import java.io.Serializable;
import java.util.List;

public interface DeletableByQueryResource<T, ID extends Serializable> {

    @DeleteResourceByQuery
    PersistenceResult<List<PersistenceResult<T>>> deleteByQueryCriteria(DeleteRequest<T, ID> request);

}
