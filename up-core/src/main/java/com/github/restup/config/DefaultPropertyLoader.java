package com.github.restup.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class DefaultPropertyLoader implements PropertyLoader {

    private static Properties loadProperties(URL resource) throws IOException {
        Properties result = new Properties();
        if (resource != null) {
            try (InputStream stream = resource.openStream()) {
                result.load(stream);
            }
        }
        return result;
    }

    @Override
    public Properties getProperties() {
        try {
            return loadProperties(
                ClassLoader.getSystemClassLoader().getResource("up.properties"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
