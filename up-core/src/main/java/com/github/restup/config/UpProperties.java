package com.github.restup.config;

import java.util.Properties;

public enum UpProperties implements PropertyLoader {

    instance();

    final Properties properties;

    UpProperties() {
        properties = new DefaultPropertyLoader().getProperties();
    }

    @Override
    public Properties getProperties() {
        return properties;
    }
}
