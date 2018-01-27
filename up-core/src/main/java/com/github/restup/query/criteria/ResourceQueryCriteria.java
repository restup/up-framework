package com.github.restup.query.criteria;

import java.util.List;
import com.google.common.collect.ImmutableList;

/**
 * Defines query criteria
 */
public interface ResourceQueryCriteria {

    /**
     * @return true if the criteria is met, false otherwise
     */
    boolean filter(Object t);

    static ResourceQueryCriteria and(ResourceQueryCriteria... criteria) {
        return and(ImmutableList.copyOf(criteria));
    }

    static ResourceQueryCriteria and(List<ResourceQueryCriteria> criteria) {
        return new AndCriteria(criteria);
    }

    static ResourceQueryCriteria or(ResourceQueryCriteria... criteria) {
        return or(ImmutableList.copyOf(criteria));
    }

    static ResourceQueryCriteria or(List<ResourceQueryCriteria> criteria) {
        return new OrCriteria(criteria);
    }
    
}
