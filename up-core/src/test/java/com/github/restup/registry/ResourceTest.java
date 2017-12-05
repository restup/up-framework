package com.github.restup.registry;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ResourceTest {

    ResourceRegistry registry = ResourceRegistryTest.registry();

    @Test
    public void testDefaultBuilderTyped() {
        test(Resource.builder(Foo.class, Long.class));
    }

    @Test
    public void testDefaultBuilder() {
        test(Resource.builder(Foo.class));
    }

    public <T> void test(Resource.Builder<T, ?> builder) {
        Resource<T, ?> r = builder
                .name("cactus")
                .pluralName("cacti")
                .registry(registry)
                .build();

        assertEquals("cactus", r.getName());
        assertEquals("cacti", r.getPluralName());
    }

    @SuppressWarnings("unused")
    private final static class Foo {

        private String id;
        private String foo;
        private String bar;
    }

}
