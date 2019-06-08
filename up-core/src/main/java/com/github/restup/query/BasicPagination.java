package com.github.restup.query;

/**
 * Represents Pagination for default settings and requests
 *
 * @author abuttaro
 */
class BasicPagination implements Pagination {

    // pagination
    private final Integer maxLimit;
    private final Integer limit;
    private final Integer offset;
    private final String key;
    private final boolean pagingDisabled;
    private final boolean withTotalsDisabled;

    /**
     * Paging enabled with provided limit, offset, totalsEnabled
     * @param limit
     * @param offset
     * @param withTotalsDisabled
     */
    BasicPagination(Integer maxLimit, Integer limit, Integer offset, String key,
        boolean withTotalsDisabled) {
        super();
        this.maxLimit = maxLimit;
        this.limit = limit;
        this.offset = offset;
        this.key = key;
        this.withTotalsDisabled = withTotalsDisabled;
        pagingDisabled = false;
    }

    /**
     * Paging enabled with provided limit, offset, totalsEnabled
     * 
     * @param limit
     * @param offset
     * @param withTotalsDisabled
     */
    BasicPagination(Integer limit, Integer offset, boolean withTotalsDisabled) {
        this(limit, limit, offset, null, withTotalsDisabled);
    }

    /**
     * Paging & totals disabled & null limit & offset
     */
    BasicPagination() {
        super();
        maxLimit = null;
        limit = null;
        offset = null;
        key = null;
        pagingDisabled = true;
        withTotalsDisabled = true;
    }

    @Override
    public Integer getMaxLimit() {
        return maxLimit;
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
    public String getKey() {
        return key;
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
