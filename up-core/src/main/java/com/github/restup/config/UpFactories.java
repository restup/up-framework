package com.github.restup.config;

import java.util.List;

public interface UpFactories {

    static UpFactories getInstance() {
        return DefaultUpFactories.instance;
    }

    <T> List<T> getInstances(ConfigurationContext configurationContext, Class<T> clazz);


}
