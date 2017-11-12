package com.github.restup.controller.model;

import com.model.test.company.Company;
import com.model.test.company.Person;
import com.github.restup.errors.ErrorObjectException;
import com.github.restup.errors.RequestError;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.registry.TestRegistry;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@SuppressWarnings({"rawtypes"})
public class ResourceControllerRequestTest {

    @Test
    public void testItemResource() {
        Builder b = path("/peeps/123");
        assertEquals("person", b.resource.getName());
        assertEquals(Arrays.asList(123l), b.ids);
    }


    @Test
    public void testErrorUnknownResource() {
        pathError("INVALID_RESOURCE_PATH", "people", "people");
        pathError("INVALID_RESOURCE_PATH", "/people", "people");
        pathError("INVALID_RESOURCE_PATH", "/people/123", "people");
        pathError("INVALID_RESOURCE_PATH", "/people/123/foo", "people");
        pathError("INVALID_RESOURCE_PATH", "/who/knows/people/123/foo", "people");
    }

    @Test
    public void testItemResourceIds() {
        Builder b = path("/companies/1,2,3");
        assertEquals("company", b.resource.getName());
        assertEquals(Arrays.asList("1", "2", "3"), b.ids);
    }

    @Test
    public void testCollectionResource() {
        Builder b = path("/peeps");
        assertEquals("person", b.resource.getName());

        b = path("/companies");
        assertEquals("company", b.resource.getName());
    }

    @Test
    public void testRelationshipList() {
        Builder b = path("/companies/1/peeps");
        assertEquals("person", b.resource.getName());
        assertEquals("company", b.relationship.getName());
        assertEquals(Arrays.asList("1"), b.ids);
    }

    @Test
    public void testRelationshipItem() {
        Builder b = path("/peeps/123/company");
        assertEquals("company", b.resource.getName());
        assertEquals("person", b.relationship.getName());
        assertEquals(Arrays.asList(123l), b.ids);
    }

    private Builder path(String path) {
        ResourceRegistry registry = TestRegistry.registry();
        registry.registerResource(Resource.builder(Company.class)
                .pluralName("companies"));
        registry.registerResource(Resource.builder(Person.class)
                .name("person").pluralName("peeps"));
        Builder b = new Builder()
                .setRequestPath(path)
                .setRegistry(registry);
        b.parsePath();
        return b;
    }


    private void pathError(String code, String path, String resourceName) {
        try {
            path(path);
            fail("Expected error");
        } catch (ErrorObjectException e) {
            RequestError err = e.getErrors().iterator().next();
            assertEquals(code, err.getCode());
            Map m = (Map) err.getMeta();
            assertEquals(resourceName, m.get("resource"));
        }
    }


    private final static class Builder extends ResourceControllerRequest.AbstractBuilder<Builder, ParsedResourceControllerRequest> {

        public ParsedResourceControllerRequest build() {
            return null;
        }
    }
}
