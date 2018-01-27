package com.github.restup.query;

/**
 * Represents Pagination for default settings and requests
 *
 * @author abuttaro
 */
class BasicPagination implements Pagination {

    // pagination
    private final Integer limit;
    private final Integer offset;
    private final boolean pagingDisabled;
    private final boolean withTotalsDisabled;

    /**
     * Paging enabled with provided limit, offset, totalsEnabled
     * @param limit
     * @param offset
     * @param withTotalsDisabled
     */
    BasicPagination(Integer limit, Integer offset, boolean withTotalsDisabled) {
        super();
        this.limit = limit;
        this.offset = offset;
        this.withTotalsDisabled = withTotalsDisabled;
        this.pagingDisabled = false;
    }

    /**
     * Paging & totals disabled & null limit & offset
     */
    BasicPagination() {
        super();
        this.limit = null;
        this.offset = null;
        this.pagingDisabled = true;
        this.withTotalsDisabled = true;
    }

    @Override
    public Integer getLimit() {
        return limit;
    }

    @Override
    public Integer getOffset() {
        return offset;
    }

    @Override
    public boolean isPagingDisabled() {
        return pagingDisabled;
    }

    @Override
    public boolean isWithTotalsDisabled() {
        return withTotalsDisabled;
    }

}
