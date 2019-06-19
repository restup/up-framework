package com.github.restup.service.model.response;

import com.github.restup.annotations.model.StatusCode;
import com.github.restup.annotations.model.StatusCodeProvider;
import java.util.List;

class BasicPersistenceResult<T> extends AbstractBasicResourceResult<T> implements
    PersistenceResult<T> {

    private final T data;
    private final StatusCodeProvider statusCodeProvider;

    BasicPersistenceResult(T data, List<RelatedResourceResult<?, ?>> relatedResourceResults,
        StatusCodeProvider statusCodeProvider) {
        super(relatedResourceResults);
        this.data = data;
        this.statusCodeProvider = statusCodeProvider;
    }

    @Override
    public T getData() {
        return data;
    }

    @Override
    public StatusCode getStatusCode() {
        return statusCodeProvider == null ? null : statusCodeProvider.getStatusCode();
    }

    public StatusCodeProvider getStatusCodeProvider() {
        return statusCodeProvider;
    }
}
