package com.github.restup.registry;

import static com.github.restup.util.TestRegistries.mapBackedRegistry;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import com.github.restup.annotations.ApiName;

public class ResourceTest {


    @Test
    public void testToString() {
        assertEquals("foo", foo().toString());
    }

    @Test
    public void testGetRelationshipsTo() {
        Resource<Foo, Long> resource = foo();
        assertEquals(0, resource.getRelationshipsTo().size());


        ResourceRelationship<?, ?, ?, ?> relationship = mock(ResourceRelationship.class);
        List<ResourceRelationship<?, ?, ?, ?>> relationships = Arrays.asList(relationship);

        ResourceRegistry registry = mock(ResourceRegistry.class);
        when(registry.getSettings()).thenReturn(mapBackedRegistry().getSettings());
        when(registry.getRelationships("foo")).thenReturn(relationships);

        resource = foo(registry);
        assertEquals(relationships, resource.getRelationships());
        assertEquals(0, resource.getRelationshipsTo().size());

        when(relationship.isTo(resource)).thenReturn(true);
        assertEquals(relationships, resource.getRelationshipsTo());
    }

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
                .registry(mapBackedRegistry())
                .build();

        assertEquals("cactus", r.getName());
        assertEquals("cacti", r.getPluralName());
    }

    private Resource<Foo, Long> foo() {
        return foo(mapBackedRegistry());
    }

    private Resource<Foo, Long> foo(ResourceRegistry registry) {
        return Resource.builder(Foo.class, Long.class).registry(registry).build();
    }

    @SuppressWarnings("unused")
    @ApiName("foo")
    private final static class Foo {

        private String id;
        private String foo;
        private String bar;
    }

}
