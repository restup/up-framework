package com.github.restup.controller.model;

import static com.github.restup.util.TestRegistries.mapBackedRegistry;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.assertEquals;

import com.github.restup.controller.mock.MockResourceControllerRequest;
import com.github.restup.controller.request.parser.path.DefaultRequestPathParser;
import com.github.restup.controller.request.parser.path.RequestPathParserResult;
import com.github.restup.errors.RequestError;
import com.github.restup.errors.RequestErrorException;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.model.test.company.Company;
import com.model.test.company.Person;
import java.util.Arrays;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class ResourceControllerRequestTest {

    @Test
    public void testItemResource() {
        RequestPathParserResult b = path("/peeps/123");
        assertEquals("person", b.getResource().getName());
        assertEquals(Arrays.asList(123l), b.getIds());
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
        RequestPathParserResult b = path("/companies/1,2,3");
        assertEquals("company", b.getResource().getName());
        assertEquals(Arrays.asList("1", "2", "3"), b.getIds());
    }

    @Test
    public void testCollectionResource() {
        RequestPathParserResult b = path("/peeps");
        assertEquals("person", b.getResource().getName());

        b = path("/companies");
        assertEquals("company", b.getResource().getName());
    }

    @Test
    public void testRelationshipList() {
        RequestPathParserResult b = path("/companies/1/peeps");
        assertEquals("person", b.getResource().getName());
        assertEquals("company", b.getRelationship().getName());
        assertEquals(Arrays.asList("1"), b.getIds());
    }

    @Test
    public void testRelationshipItem() {
        RequestPathParserResult b = path("/peeps/123/company");
        assertEquals("company", b.getResource().getName());
        assertEquals("person", b.getRelationship().getName());
        assertEquals(Arrays.asList(123l), b.getIds());
    }

    private RequestPathParserResult path(String path) {
        ResourceRegistry registry = mapBackedRegistry();
        registry.registerResource(Resource.builder(Company.class)
                .pluralName("companies"));
        registry.registerResource(Resource.builder(Person.class)
                .name("person").pluralName("peeps"));
        DefaultRequestPathParser parser = new DefaultRequestPathParser(registry);
        return parser.parsePath(
            MockResourceControllerRequest.builder()
                .url(path)
                .registry(registry)
                .build());
    }

    private void pathError(String code, String path, String resourceName) {
        Throwable thrownException = catchThrowable( () -> path(path));
        
        Assertions.assertThat(thrownException)
                .isInstanceOf(RequestErrorException.class)
                .hasFieldOrPropertyWithValue("code", code)
                .hasFieldOrPropertyWithValue("httpStatus", 404)
                .satisfies( e -> assertMeta(e, "resource", resourceName))
                .hasNoCause();
    }
    
    private void assertMeta(Throwable e, String key, String value) {
        RequestError err = ((RequestErrorException)e).getErrors().iterator().next();
        Map m = (Map) err.getMeta();
        assertEquals(value, m.get(key));
    }
}
