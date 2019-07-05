package com.github.restup.config.properties;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfilePropertiesLoader implements PropertiesLoader {

    private final static Logger log = LoggerFactory.getLogger(ProfilePropertiesLoader.class);

    private final PropertiesLoader root;

    private ProfilePropertiesLoader(PropertiesLoader root) {
        this.root = root;
    }

    public static PropertiesLoader of(PropertiesLoader root) {
        return new ProfilePropertiesLoader(root);
    }

    private static Properties merge(Properties result, String... profileNames) {
        List<Properties> list = new ArrayList();
        list.add(result);
        for (String profileName : profileNames) {
            String propertyName = "/up-" + profileName + ".properties";
            URL resource = ClassLoader.getSystemClassLoader().getResource(propertyName);
            if (resource != null) {
                try {
                    list.add(DefaultPropertiesLoader.loadProperties(resource));
                } catch (IOException e) {
                    log.info("Skipping profile" + propertyName, e);
                }
            } else {
                log.info("Ignoring missing profile " + propertyName);
            }
        }
        return PropertiesLoader.merge(list);
    }

    @Override
    public Properties getProperties() {
        Properties properties = root.getProperties();
        String[] profileNames = getProfileNames(properties);
        return merge(properties, profileNames);
    }

    private String[] getProfileNames(Properties properties) {
        String profileConfig = properties.getProperty("up.profiles", "default");
        return profileConfig.split(",");
    }

}
