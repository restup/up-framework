package com.github.restup.config;

import static com.github.restup.bind.converter.StringToBooleanConverter.isTrue;
import static com.github.restup.util.UpUtils.ifEmpty;

import com.github.restup.config.properties.PropertiesLoader;
import java.util.Properties;

public interface ConfigurationContext {

    String ASYNC_CONTROLLER = "up.controller.async";
    String AUTO_DETECT_DISABLED = "up.serialization.autoDetection.disabled";
    String BASEPATH = "up.basePath";
    String DEFAULT_MEDIA_TYPE = "up.mediaType.default";
    String PACKAGES_TO_SCAN = "up.packagesToScan";
    String PAGE_LIMIT_DEFAULT = "up.pageLimit.default";
    String PAGE_LIMIT_MAX = "up.pageLimit.max";
    String PAGINATION_DISABLED = "up.pagination.disabled";
    String PAGINATION_TOTALS_DISABLED = "up.pagination.totals.disabled";
    String PARAM_NAME_FIELDS = "up.paramName.fields";
    String PARAM_NAME_FILTER = "up.paramName.filter";
    String PARAM_NAME_INCLUDE = "up.paramName.include";
    String PARAM_NAME_LIMIT = "up.paramName.limit";
    String PARAM_NAME_OFFSET = "up.paramName.offset";
    String PARAM_NAME_PAGE_NUMBER = "up.paramName.pageNumber";
    String PARAM_NAME_SORT = "up.paramName.sort";

    String EXLCLUDE_FRAMEWORK_FILTERS = "up.excludeFrameworkFilters";

    static ConfigurationContext of(PropertiesLoader propertiesLoader) {
        return of(propertiesLoader.getProperties());
    }

    static ConfigurationContext of(Properties properties) {
        return new PropertiesConfigurationContext(properties);
    }

    static ConfigurationContext getDefault() {
        PropertiesLoader propertiesLoader = PropertiesLoader.getDefault();
        return ConfigurationContext.of(propertiesLoader.getProperties());
    }

    String getProperty(String key);

    default String getProperty(String key, String defaultValue) {
        return ifEmpty(getProperty(key), defaultValue);
    }

    default boolean getProperty(String key, boolean defaultValue) {
        return isTrue(getProperty(key), defaultValue);
    }

    default Integer getProperty(String key, Integer defaultValue) {
        String value = getProperty(key);
        return value != null ? Integer.valueOf(value) : defaultValue;
    }

}
