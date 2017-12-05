package com.github.restup.path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.github.restup.registry.ResourceRegistry;
import com.model.test.company.Address;
import com.model.test.company.Person;
import org.junit.Test;

public class MappedFieldPathValueTest {

    @Test
    @SuppressWarnings("unchecked")
    public void testReadWriteValue() {
        Person person = new Person();
        MappedFieldPathValue<Object> pv = mfpv(Person.class, "address");
        assertNull(pv.readValue(null));
        assertNull(pv.readValue(person));
        Address address = new Address();
        pv.writeValue(person, address);
        assertEquals(address, pv.readValue(person));
    }

    @SuppressWarnings("rawtypes")
    private MappedFieldPathValue mfpv(Class<Person> c, String path) {
        return ResourcePath.path(ResourceRegistry.getInstance(), c, path).firstMappedField();
    }

}
