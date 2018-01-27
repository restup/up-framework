package com.github.restup.controller.model;

import static com.github.restup.util.TestRegistries.mapBackedRegistry;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import com.github.restup.errors.ErrorObjectException;
import com.github.restup.errors.RequestError;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.model.test.company.Company;
import com.model.test.company.Person;

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

    private Builder path(String path)  {
        ResourceRegistry registry = mapBackedRegistry();
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
        Throwable thrownException = catchThrowable( () -> path(path));
        
        Assertions.assertThat(thrownException)
                .isInstanceOf(ErrorObjectException.class)
                .hasFieldOrPropertyWithValue("code", code)
                .hasFieldOrPropertyWithValue("httpStatus", 404)
                .satisfies( e -> assertMeta(e, "resource", resourceName))
                .hasNoCause();
    }
    
    private void assertMeta(Throwable e, String key, String value) {
        RequestError err = ((ErrorObjectException)e).getErrors().iterator().next();
        Map m = (Map) err.getMeta();
        assertEquals(value, m.get(key));
    }

    private final static class Builder extends ResourceControllerRequest.AbstractBuilder<Builder, ParsedResourceControllerRequest> {

        @Override
        public ParsedResourceControllerRequest build() {
            return null;
        }
    }
}
