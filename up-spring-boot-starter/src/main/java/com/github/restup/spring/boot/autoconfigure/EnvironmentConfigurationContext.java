package com.github.restup.spring.boot.autoconfigure;

import com.github.restup.config.ConfigurationContext;
import org.springframework.core.env.Environment;

public class EnvironmentConfigurationContext implements ConfigurationContext {

    private final Environment environment;
    private final ConfigurationContext delegate;

    EnvironmentConfigurationContext(Environment environment,
        ConfigurationContext delegate) {
        this.environment = environment;
        this.delegate = delegate;
    }

    public static ConfigurationContext of(Environment environment,
        ConfigurationContext configurationContext) {
        return new EnvironmentConfigurationContext(environment, configurationContext);
    }

    @Override
    public String getProperty(String key) {
        String value = environment.getProperty(key);
        if (value == null) {
            value = delegate.getProperty(key);
        }
        return value;
    }

}
