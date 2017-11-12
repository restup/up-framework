package com.github.restup.service.model.response;

public class BasicPersistenceResult<T> implements PersistenceResult<T> {

    private final T data;

    public BasicPersistenceResult(T data) {
        super();
        this.data = data;
    }

    public T getData() {
        return data;
    }

}
