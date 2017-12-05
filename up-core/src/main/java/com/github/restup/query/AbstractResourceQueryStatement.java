package com.github.restup.query;

import static com.github.restup.util.UpUtils.unmodifiableList;

import com.github.restup.query.criteria.ResourceQueryCriteria;
import com.github.restup.registry.Resource;
import java.util.List;

abstract class AbstractResourceQueryStatement {

    final Resource<?, ?> resource;
    // query criteria
    private final List<ResourceQueryCriteria> requestedCriteria;
    // sorting
    private final List<ResourceSort> requestedSort;
    // pagination
    private final Pagination pagination;

    public AbstractResourceQueryStatement(Resource<?, ?> resource, List<ResourceQueryCriteria> requestedCriteria,
            List<ResourceSort> requestedSort, Pagination pagination) {
        super();
        this.resource = resource;
        this.requestedCriteria = unmodifiableList(requestedCriteria);
        this.requestedSort = unmodifiableList(requestedSort);
        this.pagination = pagination;
    }

    public static boolean hasCriteria(AbstractResourceQueryStatement query) {
        return query != null && query.getRequestedCriteria() != null;
    }

    public boolean isPagingEnabled() {
        return pagination.isPagingEnabled();
    }

    public Resource<?, ?> getResource() {
        return resource;
    }

    public List<ResourceQueryCriteria> getRequestedCriteria() {
        return requestedCriteria;
    }

    public List<ResourceSort> getRequestedSort() {
        return requestedSort;
    }

    public Pagination getPagination() {
        return pagination;
    }

}
