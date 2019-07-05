package com.github.restup.config;

import static com.github.restup.test.ContentsAssertions.assertJson;
import static org.junit.Assert.assertEquals;

import com.github.restup.mapping.MappedClassBuilderDecorator;
import com.github.restup.mapping.fields.MappedFieldBuilderDecorator;
import com.github.restup.registry.ResourceRegistryBuilderDecorator;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.junit.Test;

public class DefaultUpFactoriesTest {

    static Map<String, List<String>> getTestFactories() throws IOException {
        Enumeration<URL> enumeration = getResourceEnumeration("/factories-test1.properties",
            "/factories-test2.properties");
        return DefaultUpFactories.loadResources(enumeration);
    }

    static Enumeration<URL> getResourceEnumeration(String... arr) {
        List<URL> resources = new ArrayList<>();
        for (String s : arr) {
            URL url = ClassLoader.class.getResource(s);
            if (url != null) {
                resources.add(url);
            }
        }
        return Collections.enumeration(resources);
    }

    @Test
    public void testLoadResources() throws IOException {
        assertJson().matches(getTestFactories());
    }

    @Test
    public void testGetInstances()
        throws IOException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        Properties properties = new Properties();
        ConfigurationContext ctx = ConfigurationContext.of(properties);
        Map<String, List<String>> factories = getTestFactories();
        assertEquals(3,
            DefaultUpFactories.getInstances(ctx, factories, MappedFieldBuilderDecorator.class)
                .size());
        assertEquals(3,
            DefaultUpFactories.getInstances(ctx, factories, MappedClassBuilderDecorator.class)
                .size());
        assertEquals(0,
            DefaultUpFactories.getInstances(ctx, factories, ResourceRegistryBuilderDecorator.class)
                .size());
    }

    @Test
    public void testGetInstancesDisabled()
        throws IOException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        Properties properties = new Properties();
        ConfigurationContext ctx = ConfigurationContext.of(properties);
        properties.setProperty(
            "com.github.restup.registry.factories.test.ADisabledClassBuilderDecorator.disabled",
            "true");
        properties.setProperty(
            "com.github.restup.registry.factories.test.ADisabledFieldBuilderDecorator.enabled",
            "false");

        Map<String, List<String>> factories = getTestFactories();
        assertEquals(2,
            DefaultUpFactories.getInstances(ctx, factories, MappedClassBuilderDecorator.class)
                .size());

        properties.setProperty(
            "com.github.restup.mapping.MappedClassBuilderDecorator.enabled",
            "false");
        assertEquals(0,
            DefaultUpFactories.getInstances(ctx, factories, MappedClassBuilderDecorator.class)
                .size());
        
        assertEquals(2,
            DefaultUpFactories.getInstances(ctx, factories, MappedFieldBuilderDecorator.class)
                .size());

        assertEquals(0,
            DefaultUpFactories.getInstances(ctx, factories, ResourceRegistryBuilderDecorator.class)
                .size());
    }

}
