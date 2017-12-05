package com.github.restup.query;

/**
 * Represents Pagination for default settings and requests
 *
 * @author abuttaro
 */
public class Pagination {

    // pagination
    private final Integer limit;
    private final Integer offset;
    private final boolean pagingDisabled;
    private final boolean withTotalsDisabled;

    public Pagination(Integer limit, Integer offset, boolean pagingDisabled, boolean withTotalsDisabled) {
        super();
        this.limit = limit;
        this.offset = offset;
        this.pagingDisabled = pagingDisabled;
        this.withTotalsDisabled = withTotalsDisabled;
    }

    public static int getStart(Pagination pagination) {
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

    public static boolean isPagedListRequired(Pagination pagination, Long totalCount) {
        return (pagination.isWithTotalsDisabled() || totalCount > 0)
                && pagination.getLimit() != null
                && pagination.getLimit() > 0;
    }

    public Integer getLimit() {
        return limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public boolean isPagingDisabled() {
        return pagingDisabled;
    }

    public boolean isPagingEnabled() {
        return !isPagingDisabled();
    }

    public boolean isWithTotalsEnabled() {
        return !isWithTotalsDisabled();
    }

    public boolean isWithTotalsDisabled() {
        return withTotalsDisabled;
    }

}
