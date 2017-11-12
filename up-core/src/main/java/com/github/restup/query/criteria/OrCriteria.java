package com.github.restup.query.criteria;

import java.util.List;

public class OrCriteria extends ListCriteria {

    public OrCriteria(List<ResourceQueryCriteria> criteria) {
        super(criteria);
    }

    @Override
    public boolean filter(Object t) {
        for ( ResourceQueryCriteria c : getCriteria() ) {
            if ( c.filter(t) ) {
                return true;
            }
        }
        return false;
    }
}
