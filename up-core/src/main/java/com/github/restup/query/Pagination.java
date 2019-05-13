package com.github.restup.query;

/**
 * Represents Pagination for default settings and requests
 *
 * @author abuttaro
 */
public interface Pagination {

    /**
     * Pagination with specified limit and offset and paging and totals enabled
     *
     * @param limit value for pagination
     * @param offset value for pagination
     * @return pagination instance
     */
    static Pagination of(Integer limit, Integer offset) {
        return of(limit, offset, false);
    }

    static Pagination of(Integer limit, Integer offset, boolean withTotalsDisabled) {
        return new BasicPagination(limit, offset, withTotalsDisabled);
    }

    static Pagination of(Integer maxLimit, Integer limit, Integer offset, boolean withTotalsDisabled) {
        return new BasicPagination(maxLimit, limit, offset, withTotalsDisabled);
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
        return (pagination.isWithTotalsDisabled() || (totalCount != null && totalCount > 0))
                && pagination.getLimit() != null
                && pagination.getLimit() > 0;
    }

    Integer getMaxLimit();

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

}
