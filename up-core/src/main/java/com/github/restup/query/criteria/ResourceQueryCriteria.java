package com.github.restup.query.criteria;

/**
 * Tag interface for query criteria
 */
public interface ResourceQueryCriteria {

    /**
     * @param t
     * @return true if the criteria is met, false otherwise
     */
    boolean filter(Object t);

}
