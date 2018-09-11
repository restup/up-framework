package com.github.restup.service.model.response;

import java.util.List;

class BasicListResult<T> extends AbstractBasicResourceResult<List<T>> implements
    ReadResult<List<T>> {

    private final List<T> data;

    BasicListResult(List<T> data, List<RelatedResourceResult<?, ?>> relatedResourceResults) {
        super(relatedResourceResults);
        this.data = data;
    }

    @Override
    public List<T> getData() {
        return data;
    }

}
