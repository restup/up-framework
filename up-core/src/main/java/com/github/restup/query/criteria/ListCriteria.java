package com.github.restup.query.criteria;

import java.util.List;

public class ListCriteria implements ResourceQueryCriteria {

    private final List<ResourceQueryCriteria> criteria;

    public ListCriteria(List<ResourceQueryCriteria> criteria) {
        this.criteria = criteria;
    }

    public List<ResourceQueryCriteria> getCriteria() {
        return criteria;
    }

    @Override
    public boolean filter(Object t) {
        for ( ResourceQueryCriteria c : criteria ) {
            if ( ! c.filter(t) ) {
                return false;
            }
        }
        return true;
    }
}
