package com.github.restup.service.model.response;

import java.util.List;
import com.github.restup.query.Pagination;

class BasicPagedResult<T> extends BasicListResult<T> implements PagedResult<T> {

    private final Pagination pagination;
    private final Long total;

    BasicPagedResult(List<T> data, Pagination pagination, Long totalRecords) {
        super(data);
        this.pagination = pagination;
        this.total = totalRecords;
    }

    @Override
    public Integer getLimit() {
        return pagination == null ? null : pagination.getLimit();
    }

    @Override
    public Integer getOffset() {
        return pagination == null ? null : pagination.getOffset();
    }

    @Override
    public Long getTotal() {
        return total;
    }
    
    public Pagination getPagination() {
        return pagination;
    }

}
