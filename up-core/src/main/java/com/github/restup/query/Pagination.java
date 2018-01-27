package com.github.restup.query;

/**
 * Represents Pagination for default settings and requests
 *
 * @author abuttaro
 */
public interface Pagination {

    Integer getLimit();

    Integer getOffset();

    boolean isPagingDisabled();

    default boolean isPagingEnabled() {
        return !isPagingDisabled();
    }

    default boolean isWithTotalsEnabled() {
        return !isWithTotalsDisabled();
    }

    boolean isWithTotalsDisabled();

    /**
     * Pagination with specified limit and offset and paging and totals enabled
     * @param limit
     * @param offset
     * @return
     */
    static Pagination of(Integer limit, Integer offset) {
        return of(limit, offset, false);
    }
    
    static Pagination of(Integer limit, Integer offset, boolean withTotalsDisabled) {
        return new BasicPagination(limit, offset, withTotalsDisabled);
    }

    static Pagination disabled() {
        return new BasicPagination();
    }

    static int getStart(Pagination pagination) {
        Integer offset = pagination.getOffset();
        if (offset == null) {
            offset = 0;
        }

        Integer pageSize = pagination.getLimit();
        if (pageSize == null) {
            pageSize = 10;
        }

        return offset * pageSize;
    }

    static boolean isPagedListRequired(Pagination pagination, Long totalCount) {
        return (pagination.isWithTotalsDisabled() || totalCount > 0)
                && pagination.getLimit() != null
                && pagination.getLimit() > 0;
    }

}
