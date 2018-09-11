package com.github.restup.service.model.response;

import java.util.List;

abstract class AbstractBasicResourceResult<T> implements ResourceResult<T> {

    private final List<RelatedResourceResult<?, ?>> relatedResourceResults;

    AbstractBasicResourceResult(List<RelatedResourceResult<?, ?>> relatedResourceResults) {
        this.relatedResourceResults = relatedResourceResults;
    }

    @Override
    public List<RelatedResourceResult<?, ?>> getRelatedResourceResults() {
        return relatedResourceResults;
    }

}
