package com.github.restup.service.model.response;

import java.util.List;
import com.github.restup.query.Pagination;

public interface PagedResult<T> extends ResourceResult<List<T>> {

    Integer getOffset();

    Integer getLimit();

    Long getTotal();

    static <T> PagedResult<T> of(List<T> result, Pagination pagination, Long totalRecords) {
        return new BasicPagedResult<>(result, pagination, totalRecords);
    }

}
