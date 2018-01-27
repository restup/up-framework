package com.github.restup.service.model.response;

import java.util.List;

/**
 * Response interface for read results
 */
public interface ReadResult<T> extends ResourceResult<T> {

    static <T> ReadResult<T> of(T data) {
        return new BasicReadResult<T>(data);
    }

    static <T> BasicListResult<T> of(List<T> data) {
        return new BasicListResult<T>(data);
    }

}
