package com.github.restup.service.model.response;

import java.util.List;

class BasicReadResult<T> extends AbstractBasicResourceResult<T> implements ReadResult<T> {

    private final T data;

    BasicReadResult(T data, List<RelatedResourceResult<?, ?>> relatedResourceResults) {
        super(relatedResourceResults);
        this.data = data;
    }

    @Override
    public T getData() {
        return data;
    }

}
