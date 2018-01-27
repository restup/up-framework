package com.github.restup.path;

import static com.github.restup.util.TestRegistries.defaultRegistry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import com.github.restup.test.assertions.Assertions;
import com.model.test.company.Address;
import com.model.test.company.Person;
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
    
    @Test
    public void testHashCodeEquals() {
        Assertions.assertHashCodeEquals(MappedFieldPathValue.class);
    }

    @SuppressWarnings("rawtypes")
    private MappedFieldPathValue mfpv(Class<Person> c, String path) {
        return ResourcePath.path(defaultRegistry(), c, path).firstMappedField();
    }

}
