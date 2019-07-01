package com.github.restup.config;

import static com.github.restup.bind.converter.StringToBooleanConverter.isTrue;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public enum UpFactories {

    instance;

    final Map<String, List<String>> resources;

    UpFactories() {
        try {
            resources = loadResources(
                ClassLoader.getSystemClassLoader().getResources("/META-INF/up.factories"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static <T> List<T> getInstances(Properties properties, Map<String, List<String>> resources,
        Class<T> clazz)
        throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        List<T> result = new ArrayList<>();
        String key = clazz.getName();
        if (isEnabled(key, properties)) {
            List<String> classes = resources.get(key);
            if (classes != null) {
                for (String s : classes) {
                    if (isEnabled(s, properties)) {
                        Class<?> c = Class.forName(s);
                        if (clazz.isAssignableFrom(c)) {
                            result.add((T) c.newInstance());
                        } else {
                            throw new IllegalStateException(
                                s + " is not of type " + clazz.getName());
                        }
                    }
                }
            }
        }
        return result;
    }

    private static boolean isEnabled(String s, Properties properties) {
        boolean enabled = isTrue(properties.get(s + ".enabled"), true);
        boolean disabled = isTrue(properties.get(s + ".disabled"), false);
        return enabled && !disabled;
    }

    static Map<String, List<String>> loadResources(Enumeration<URL> resources) throws IOException {
        List<Properties> properties = toPropertiesList(resources);
        return merge(properties);
    }

    static Map<String, List<String>> merge(List<Properties> properties) {
        Map<String, List<String>> map = new HashMap<>();
        for (Properties p : properties) {
            for (Entry e : p.entrySet()) {
                String[] array = parseValue(e.getValue());
                if (isNotEmpty(array)) {
                    List<String> list = map.get(e.getKey());
                    if (list == null) {
                        list = new ArrayList<>();
                        map.put(e.getKey().toString(), list);
                    }
                    for (String s : array) {
                        list.add(s);
                    }
                }
            }
        }
        return map;
    }

    static String[] parseValue(Object value) {
        if (value instanceof String) {
            return ((String) value).split(",");
        }
        return null;
    }

    static List<Properties> toPropertiesList(Enumeration<URL> resources) throws IOException {
        List<Properties> list = new ArrayList<>();
        while (resources.hasMoreElements()) {
            Properties properties = new Properties();
            try (InputStream stream = resources.nextElement().openStream()) {
                properties.load(stream);
            }
            list.add(properties);
        }
        return list;
    }

    public <T> List<T> getInstances(Class<T> clazz) {
        return getInstances(UpProperties.instance.getProperties(), clazz);
    }

    <T> List<T> getInstances(Properties properties, Class<T> clazz) {
        try {
            return getInstances(properties, resources, clazz);
        } catch (Exception e) {
            throw new IllegalStateException("Invalid factory configuration.", e);
        }
    }

}
