package com.github.restup.service.model.response;

import com.github.restup.query.Pagination;
import java.util.List;

public class BasicPagedResult<T> extends BasicListResult<T> implements PagedResult<T> {

    private final Pagination pagination;
    private Long total;

    public BasicPagedResult(List<T> data, Pagination pagination, Long totalRecords) {
        super(data);
        this.pagination = pagination;
        this.total = totalRecords;
    }

    public Integer getLimit() {
        return pagination == null ? null : pagination.getLimit();
    }

    public Integer getOffset() {
        return pagination == null ? null : pagination.getOffset();
    }

    public Long getTotal() {
        return total;
    }

}
