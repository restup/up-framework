package com.github.restup.service.model.response;

/**
 * Response interface for create, update, delete operations
 */
public interface PersistenceResult<T> extends ResourceResult<T> {

    static <T> PersistenceResult<T> of(T data) {
        return new BasicPersistenceResult<>(data);
    }

}
