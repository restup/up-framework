package com.github.restup.service.model.response;

import java.util.List;

class BasicPersistenceResult<T> extends AbstractBasicResourceResult<T> implements
    PersistenceResult<T> {

    private final T data;

    BasicPersistenceResult(T data, List<RelatedResourceResult<?, ?>> relatedResourceResults) {
        super(relatedResourceResults);
        this.data = data;
    }

    @Override
    public T getData() {
        return data;
    }

}
