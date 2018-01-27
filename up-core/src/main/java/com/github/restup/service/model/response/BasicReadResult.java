package com.github.restup.service.model.response;

class BasicReadResult<T> implements ReadResult<T> {

    private final T data;

    BasicReadResult(T data) {
        super();
        this.data = data;
    }

    @Override
    public T getData() {
        return data;
    }

}
