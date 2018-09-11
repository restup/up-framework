package com.github.restup.service.model.response;

import java.util.Collections;
import java.util.List;

/**
 * Response interface for read results
 */
public interface ReadResult<T> extends ResourceResult<T> {

    static <T> ReadResult<T> of(T data) {
        return new BasicReadResult<>(data, Collections.emptyList());
    }

    static <T> ReadResult<List<T>> of(List<T> data) {
        return new BasicListResult<>(data, Collections.emptyList());
    }

    static <T> ReadResult<T> of(ReadResult<T> result,
        List<RelatedResourceResult<?, ?>> relatedResourceResults) {
        if (result.getData() instanceof List) {
            return new BasicListResult<>((List) result.getData(), relatedResourceResults);
        }
        return new BasicReadResult<>(result.getData(), relatedResourceResults);
    }

}
