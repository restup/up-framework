package com.github.restup.service.model.response;

public class BasicReadResult<T> implements ReadResult<T> {

    private final T data;

    public BasicReadResult(T data) {
        super();
        this.data = data;
    }

    public T getData() {
        return data;
    }

}
