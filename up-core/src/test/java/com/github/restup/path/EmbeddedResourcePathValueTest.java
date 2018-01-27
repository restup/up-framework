package com.github.restup.path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.Test;
import com.github.restup.registry.Resource;
import com.model.test.company.Company;
import com.model.test.company.Person;

public class EmbeddedResourcePathValueTest {

    @Test
    public void testPath() {
        String path = "foo";
        EmbeddedResourcePathValue pathValue = new EmbeddedResourcePathValue(null, path);
        assertNull(pathValue.getBeanPath());
        assertNull(pathValue.getPersistedPath());
        assertEquals(path, pathValue.getApiPath());
    }

    @Test
    public void testSupportsType() {
        Resource<?, ?> resource = mock(Resource.class);
        when(resource.getType()).thenReturn(Person.class);
        EmbeddedResourcePathValue pathValue = new EmbeddedResourcePathValue(resource, "foo");
        assertTrue(pathValue.supportsType(Person.class));
        assertFalse(pathValue.supportsType(Company.class));
    }

}
