package com.github.restup.service.model.response;

import com.github.restup.query.Pagination;
import java.util.Collections;
import java.util.List;

public interface PagedResult<T> extends ResourceResult<List<T>> {

    static <T> PagedResult<T> of(List<T> result, Pagination pagination, Long totalRecords) {
        return new BasicPagedResult<>(result, pagination, totalRecords, Collections.emptyList());
    }

    static <T> PagedResult<T> of(PagedResult<T> result,
        List<RelatedResourceResult<?, ?>> relatedResourceResults) {
        return new BasicPagedResult<>(result.getData(), result.getPagination(), result.getTotal(),
            relatedResourceResults);
    }

    default Integer getOffset() {
        Pagination pagination = getPagination();
        return pagination == null ? null : pagination.getOffset();
    }

    default Integer getLimit() {
        Pagination pagination = getPagination();
        return pagination == null ? null : pagination.getLimit();
    }

    Pagination getPagination();

    Long getTotal();

}
