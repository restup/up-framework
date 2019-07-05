package com.github.restup.config;

import java.util.Properties;

class PropertiesConfigurationContext implements ConfigurationContext {

    private final Properties properties;

    PropertiesConfigurationContext(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
