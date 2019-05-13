package com.github.restup.repository.dynamodb;

import com.github.restup.query.criteria.ResourcePathFilter;
import java.util.List;

class BasicOptimizedResourceQueryCriteria implements OptimizedResourceQueryCriteria {

    private final List<ResourcePathFilter> indexCriteria;
    private final List<ResourcePathFilter> filterCriteria;


    BasicOptimizedResourceQueryCriteria(
        List<ResourcePathFilter> indexCriteria,
        List<ResourcePathFilter> filterCriteria) {
        this.indexCriteria = indexCriteria;
        this.filterCriteria = filterCriteria;
    }

    @Override
    public List<ResourcePathFilter> getIndexCriteria() {
        return indexCriteria;
    }

    @Override
    public List<ResourcePathFilter> getFilterCriteria() {
        return filterCriteria;
    }
}
