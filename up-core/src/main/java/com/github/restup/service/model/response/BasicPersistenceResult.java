package com.github.restup.service.model.response;

class BasicPersistenceResult<T> implements PersistenceResult<T> {

    private final T data;

    BasicPersistenceResult(T data) {
        super();
        this.data = data;
    }

    @Override
    public T getData() {
        return data;
    }

}
