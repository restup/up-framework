package com.github.restup.spring.boot.autoconfigure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "up")
public class UpProperties {

    @Value("${basePath:/}")
    private String basePath;

    @Value("${offsetParamName:offset}")
    private String[] offsetParamName;
    @Value("${limitParamName:limit}")
    private String[] limitParamName;
    @Value("${sortParamName:sort}")
    private String[] sortParamName;
    @Value("${filterParamName:filter}")
    private String[] filterParamName;
    @Value("${includeParamName:include}")
    private String[] includeParamName;
    @Value("${fieldsParamName:fields}")
    private String[] fieldsParamName;
    @Value("${pageNumberParamName:}")
    private String[] pageNumberParamName;

    @Value("${maxPageLimit:100}")
    private Integer maxPageLimit;
    @Value("${defaultPageLimit:10}")
    private Integer defaultPageLimit;

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
