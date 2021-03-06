package com.github.restup.service.model.request;

import static com.github.restup.util.TestRegistries.defaultRegistry;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import org.junit.Test;
import com.github.restup.bind.param.NoOpParameterProvider;
import com.github.restup.path.ResourcePath;
import com.github.restup.registry.ResourceRegistry;
import com.model.test.company.Person;

public class BasicPersistenceRequestTest {

    @Test
    public void testHasPath() {
        
        ResourceRegistry registry = defaultRegistry();

        ResourcePath address = ResourcePath.path(registry, Person.class, "address");
        ResourcePath lastName = ResourcePath.path(registry, Person.class, "lastName");
        ResourcePath street = ResourcePath.path(registry, Person.class, "address.street");
        ResourcePath dStreet = ResourcePath.builder(registry, Person.class).data().path("address.street").build();
        ResourcePath iStreet = ResourcePath.builder(registry, Person.class).data(1).path("address.street").build();

        // test exact path
        assertTrue(request(street).hasPath(street));
        assertTrue(request(dStreet).hasPath(dStreet));
        assertTrue(request(iStreet).hasPath(iStreet));

        // check that data is ignored
        assertTrue(request(dStreet).hasPath(street));
        assertTrue(request(street).hasPath(dStreet));
        assertTrue(request(street).hasPath(iStreet));
        assertTrue(request(dStreet).hasPath(iStreet));
        assertTrue(request(iStreet).hasPath(street));
        assertTrue(request(iStreet).hasPath(dStreet));

        // Now test that subpath is considered
        assertTrue(request(address).hasPath(street));
        assertTrue(request(address).hasPath(dStreet));
        assertTrue(request(address).hasPath(iStreet));

        // but reverse is not true
        assertFalse(request(street).hasPath(address));
        assertFalse(request(dStreet).hasPath(address));
        assertFalse(request(iStreet).hasPath(address));

        // some negative tests
        assertFalse(request(lastName).hasPath(address));
        assertFalse(request(lastName).hasPath(street));
        assertFalse(request(lastName).hasPath(dStreet));
        assertFalse(request(lastName).hasPath(iStreet));

        assertTrue(request(street, dStreet, iStreet).hasPath(street));
        assertTrue(request(street, dStreet, iStreet).hasPath(dStreet));
        assertTrue(request(street, dStreet, iStreet).hasPath(iStreet));
        assertFalse(request(street, dStreet, iStreet).hasPath(lastName));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private AbstractPersistenceRequest request(ResourcePath... paths) {
        return new BasicUpdateRequest(null, null, null, Arrays.asList(paths), null, NoOpParameterProvider.getInstance());
    }

}
