package com.github.restup.service.model.response;

import java.util.List;

public class BasicListResult<T> implements ReadResult<List<T>> {

    private final List<T> data;

    public BasicListResult(List<T> data) {
        super();
        this.data = data;
    }

    public List<T> getData() {
        return data;
    }

}
