package com.github.restup.repository.dynamodb;

import com.github.restup.query.criteria.ResourcePathFilter;
import java.util.List;

class BasicOptimizedResourceQueryCriteria implements OptimizedResourceQueryCriteria {

    private final List<ResourcePathFilter> keyCriteria;
    private final String indexName;
    private final List<ResourcePathFilter> indexCriteria;
    private final List<ResourcePathFilter> filterCriteria;


    BasicOptimizedResourceQueryCriteria(
        List<ResourcePathFilter> keyCriteria,
        String indexName,
        List<ResourcePathFilter> indexCriteria,
        List<ResourcePathFilter> filterCriteria) {
        this.keyCriteria = keyCriteria;
        this.indexName = indexName;
        this.indexCriteria = indexCriteria;
        this.filterCriteria = filterCriteria;
    }

    @Override
    public List<ResourcePathFilter> getKeyCriteria() {
        return keyCriteria;
    }

    @Override
    public String getIndexName() {
        return indexName;
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
