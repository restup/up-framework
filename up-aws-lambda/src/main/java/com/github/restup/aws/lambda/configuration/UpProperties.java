package com.github.restup.aws.lambda.configuration;

public class UpProperties {

    private String basePath;

    private String[] offsetParamName = {"offset"};
    private String[] limitParamName = {"limit"};
    private String[] sortParamName = {"sort"};
    private String[] filterParamName = {"filter"};
    private String[] includeParamName = {"include"};
    private String[] fieldsParamName = {"fields"};
    private String[] pageNumberParamName;

    private Integer maxPageLimit = 100;
    private Integer defaultPageLimit = 10;

    private String defaultMediaType;
    private boolean paginationDisabled;
    private boolean paginationTotalsDisabled;
    private boolean excludeFrameworkFilters;
    private boolean disableSerializationAutoDetection;

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getDefaultMediaType() {
        return defaultMediaType;
    }

    public void setDefaultMediaType(String defaultMediaType) {
        this.defaultMediaType = defaultMediaType;
    }

    public Integer getMaxPageLimit() {
        return maxPageLimit;
    }

    public void setMaxPageLimit(Integer maxPageLimit) {
        this.maxPageLimit = maxPageLimit;
    }

    public Integer getDefaultPageLimit() {
        return defaultPageLimit;
    }

    public void setDefaultPageLimit(Integer defaultPageLimit) {
        this.defaultPageLimit = defaultPageLimit;
    }

    public boolean isPaginationDisabled() {
        return paginationDisabled;
    }

    public void setPaginationDisabled(boolean paginationDisabled) {
        this.paginationDisabled = paginationDisabled;
    }

    public boolean isPaginationTotalsDisabled() {
        return paginationTotalsDisabled;
    }

    public void setPaginationTotalsDisabled(boolean paginationTotalsDisabled) {
        this.paginationTotalsDisabled = paginationTotalsDisabled;
    }

    public boolean isExcludeFrameworkFilters() {
        return excludeFrameworkFilters;
    }

    public void setExcludeFrameworkFilters(boolean excludeFrameworkFilters) {
        this.excludeFrameworkFilters = excludeFrameworkFilters;
    }

    public boolean isDisableSerializationAutoDetection() {
        return disableSerializationAutoDetection;
    }

    public void setDisableSerializationAutoDetection(boolean disableSerializationAutoDetection) {
        this.disableSerializationAutoDetection = disableSerializationAutoDetection;
    }

    public String[] getOffsetParamName() {
        return offsetParamName;
    }

    public void setOffsetParamName(String[] offsetParamName) {
        this.offsetParamName = offsetParamName;
    }

    public String[] getLimitParamName() {
        return limitParamName;
    }

    public void setLimitParamName(String[] limitParamName) {
        this.limitParamName = limitParamName;
    }

    public String[] getSortParamName() {
        return sortParamName;
    }

    public void setSortParamName(String[] sortParamName) {
        this.sortParamName = sortParamName;
    }

    public String[] getFilterParamName() {
        return filterParamName;
    }

    public void setFilterParamName(String[] filterParamName) {
        this.filterParamName = filterParamName;
    }

    public String[] getIncludeParamName() {
        return includeParamName;
    }

    public void setIncludeParamName(String[] includeParamName) {
        this.includeParamName = includeParamName;
    }

    public String[] getFieldsParamName() {
        return fieldsParamName;
    }

    public void setFieldsParamName(String[] fieldsParamName) {
        this.fieldsParamName = fieldsParamName;
    }

    public String[] getPageNumberParamName() {
        return pageNumberParamName;
    }

    public void setPageNumberParamName(String[] pageNumberParamName) {
        this.pageNumberParamName = pageNumberParamName;
    }

}
