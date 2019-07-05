package com.github.restup.config.properties;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;

@FunctionalInterface
public interface PropertiesLoader {

    static Properties merge(Properties... arr) {
        return merge(Arrays.asList(arr));
    }

    static Properties merge(List<Properties> list) {
        Properties result = new Properties();
        for (Properties properties : list) {
            Set<String> propertyNames = properties.stringPropertyNames();
            for (String name : propertyNames) {
                String propertyValue = properties.getProperty(name);
                result.setProperty(name, propertyValue);
            }
        }
        return result;
    }

    static PropertiesLoader getDefault() {
        PropertiesLoader propertiesLoader = new DefaultPropertiesLoader();
        propertiesLoader = ProfilePropertiesLoader.of(propertiesLoader);
        return propertiesLoader;
    }

    Properties getProperties();
}
