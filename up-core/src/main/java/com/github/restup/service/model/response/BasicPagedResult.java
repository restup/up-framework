package com.github.restup.service.model.response;

import com.github.restup.query.Pagination;
import java.util.List;

class BasicPagedResult<T> extends BasicListResult<T> implements PagedResult<T> {

    private final Pagination pagination;
    private final Long total;

    BasicPagedResult(List<T> data, Pagination pagination, Long totalRecords,
        List<RelatedResourceResult<?, ?>> relatedResourceResults) {
        super(data, relatedResourceResults);
        this.pagination = pagination;
        total = totalRecords;
    }

    @Override
    public Long getTotal() {
        return total;
    }

    @Override
    public Pagination getPagination() {
        return pagination;
    }

}
