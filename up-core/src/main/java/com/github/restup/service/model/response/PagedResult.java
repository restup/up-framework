package com.github.restup.service.model.response;

import java.util.List;

public interface PagedResult<T> extends ResourceResult<List<T>> {

    Integer getOffset();

    Integer getLimit();

    Long getTotal();
}
