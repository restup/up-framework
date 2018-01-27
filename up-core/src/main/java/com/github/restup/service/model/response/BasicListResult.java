package com.github.restup.service.model.response;

import java.util.List;

class BasicListResult<T> implements ReadResult<List<T>> {

    private final List<T> data;

    BasicListResult(List<T> data) {
        super();
        this.data = data;
    }

    @Override
    public List<T> getData() {
        return data;
    }

}
