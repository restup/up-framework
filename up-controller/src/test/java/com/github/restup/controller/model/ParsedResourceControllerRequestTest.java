package com.github.restup.controller.model;

import com.model.test.company.Company;
import com.model.test.company.Person;
import com.github.restup.controller.model.ParsedResourceControllerRequest.Builder;
import com.github.restup.errors.ErrorObjectException;
import com.github.restup.errors.RequestError;
import com.github.restup.query.ResourceQueryStatement;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.registry.TestRegistry;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collection;

import static com.github.restup.controller.model.ParsedResourceControllerRequest.builder;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({"rawtypes", "unchecked"})
public class ParsedResourceControllerRequestTest {

    ResourceRegistry registry;
    @Mock
    ResourceControllerRequest details;

    private static void assertSize(int size, Collection c) {
        assertEquals(size, CollectionUtils.size(c));
    }

    @Before
    public void setup() {
        registry = TestRegistry.registry();
        registry.registerResource(Company.class);
        registry.registerResource(Resource.builder(Person.class).name("person"));
        when(details.getResource()).thenReturn((Resource) registry.getResource(Company.class));
    }

    private Builder mock() {

        return builder(registry, details);
    }

    private ResourceQueryStatement rql(Builder b) {
        ParsedResourceControllerRequest details = b.build();
        assertEquals(1, details.getRequestedQueries().size());
        return (ResourceQueryStatement) details.getRequestedQueries().get(0);
    }

    private void assertError(String code, Builder b) {
        try {
            assertSize(1, b.getErrors());
            assertTrue(b.hasErrors());
            rql(b);
            fail("Exception Expected");
        } catch (ErrorObjectException ex) {
            assertEquals(1, ex.getErrors().size());
            RequestError e = ex.getErrors().iterator().next();
            assertEquals(code, e.getCode());
        }
    }

    @Test
    public void testPagination() {
        ResourceQueryStatement rql = rql(mock().setPageOffset("offset", 1)
                .setPageLimit("limit", 100));
        assertEquals(Integer.valueOf(1), rql.getPagination().getOffset());
        assertEquals(Integer.valueOf(100), rql.getPagination().getLimit());
    }

    @Test
    public void testErrorPagination() {
        assertError("DUPLICATE_PARAMETER", mock().setPageOffset("offset", 1)
                .setPageOffset("pageNo", 1));
        assertError("DUPLICATE_PARAMETER", mock().setPageLimit("limit", 1)
                .setPageLimit("pageSize", 1));

        assertError("MIN_OFFSET", mock().setPageOffset("offset", -1));
        assertError("MIN_PAGENO", mock().setPageOffset("pageNo", -1));
        assertError("MIN_LIMIT", mock().setPageLimit("limit", -1));
        assertError("MIN_PAGESIZE", mock().setPageLimit("pageSize", -1));
        assertError("MIN_PAGENO", mock().setPageOffset("pageNo", null));
        assertError("MIN_LIMIT", mock().setPageLimit("limit", null));
        assertError("MAX_LIMIT", mock().setPageLimit("limit", 10000));
        assertError("MAX_PAGESIZE", mock().setPageLimit("pageSize", 10000));
    }

    @Test
    public void testErrorAddSort() {
        assertError("INVALID_PARAMETER_PATH", mock().addSort("sort", "-foo", "foo", false));
    }

    @Test
    public void testFieldRequest() {
        ResourceQueryStatement rql = rql(mock().setFieldRequest("fields[person]", "name", "person", ResourceQueryStatement.Type.Sparse));
        assertEquals(ResourceQueryStatement.Type.Sparse, rql.getType());
    }

    @Test
    public void testFieldRequestByResource() {
        ResourceQueryStatement rql = rql(mock().setFieldRequest("fields[company]", "name", registry.getResource(Company.class), ResourceQueryStatement.Type.Default));
        assertEquals(ResourceQueryStatement.Type.Default, rql.getType());
    }

    @Test
    public void testErrorUnknownResource() {
        assertError("INVALID_PARAMETER", mock().setFieldRequest("fields[foo]", "name", "foo", ResourceQueryStatement.Type.Default));
        assertError("INVALID_PARAMETER", mock().addRequestedField("fields", "firstName", "goo", "firstName"));
        assertError("INVALID_PARAMETER", mock().addAdditionalField("fields", "+firstName", "boo", "firstName"));
        assertError("INVALID_PARAMETER", mock().addExcludedField("fields", "-firstName", "too", "firstName"));
    }

    @Test
    public void testAdditionalField() {
        ResourceQueryStatement rql = rql(mock().addAdditionalField("fields", "+firstName", "person", "firstName")
                .addAdditionalField("fields", "+lastName", "person", "lastName"));
        assertTrue(rql.hasRequestedPathsAdded("firstName"));
        assertTrue(rql.hasRequestedPathsAdded("lastName"));
        assertFalse(rql.hasRequestedPathsAdded("address"));
        assertSize(0, rql.getRequestedPaths());
        assertSize(2, rql.getRequestedPathsAdded());
        assertSize(0, rql.getRequestedPathsExcluded());
    }

    @Test
    public void testExcludedField() {
        ResourceQueryStatement rql = rql(mock().addExcludedField("fields", "-firstName", "person", "firstName")
                .addExcludedField("fields", "-address", "person", "address"));
        assertTrue(rql.hasRequestedPathsExcluded("address"));
        assertTrue(rql.hasRequestedPathsExcluded("firstName"));
        assertFalse(rql.hasRequestedPathsExcluded("lastName"));
        assertSize(0, rql.getRequestedPaths());
        assertSize(0, rql.getRequestedPathsAdded());
        assertSize(2, rql.getRequestedPathsExcluded());
    }

    @Test
    public void testRequestedField() {
        ResourceQueryStatement rql = rql(mock()
                .addRequestedField("fields", "address", "person", "address")
                .addRequestedField("fields", "firstName", "person", "firstName"));
        assertTrue(rql.hasRequestedPaths("address"));
        assertTrue(rql.hasRequestedPaths("firstName"));
        assertFalse(rql.hasRequestedPaths("lastName"));
        assertSize(2, rql.getRequestedPaths());
        assertSize(0, rql.getRequestedPathsAdded());
        assertSize(0, rql.getRequestedPathsExcluded());
        assertSize(0, rql.getRequiredRelationshipPaths());
    }

}
