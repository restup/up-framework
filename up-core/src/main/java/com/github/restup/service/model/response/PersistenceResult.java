package com.github.restup.service.model.response;

import com.github.restup.annotations.model.StatusCodeProvider;
import java.util.Collections;
import java.util.List;

/**
 * Response interface for create, update, delete operations
 */
public interface PersistenceResult<T> extends ResourceResult<T>, StatusCodeProvider {

    static <T> PersistenceResult<T> of(T data) {
        return of(data, null);
    }

    static <T> PersistenceResult<T> of(T data, StatusCodeProvider statusCodeProvider) {
        return new BasicPersistenceResult<>(data, Collections.emptyList(), statusCodeProvider);
    }

    static <T> PersistenceResult<T> of(PersistenceResult<T> result,
        List<RelatedResourceResult<?, ?>> relatedResourceResults) {
        return new BasicPersistenceResult<>(result.getData(), relatedResourceResults, null);
    }

}
