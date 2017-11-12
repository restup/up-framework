package com.github.restup.service.model.request;

import java.util.List;

public class BulkRequest<T> {

    private final List<T> data;

    public BulkRequest(List<T> data) {
        super();
        this.data = data;
    }

    public List<T> getData() {
        return data;
    }

}
