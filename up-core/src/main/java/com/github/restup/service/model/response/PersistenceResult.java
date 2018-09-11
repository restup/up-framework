package com.github.restup.service.model.response;

import java.util.Collections;
import java.util.List;

/**
 * Response interface for create, update, delete operations
 */
public interface PersistenceResult<T> extends ResourceResult<T> {

    static <T> PersistenceResult<T> of(T data) {
        return new BasicPersistenceResult<>(data, Collections.emptyList());
    }

    static <T> PersistenceResult<T> of(PersistenceResult<T> result,
        List<RelatedResourceResult<?, ?>> relatedResourceResults) {
        return new BasicPersistenceResult<>(result.getData(), relatedResourceResults);
    }

}
