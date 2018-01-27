package com.github.restup.mapping.fields.composition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.lang.reflect.Method;
import org.junit.Test;
import com.github.restup.assertions.Assertions;
import com.model.test.company.Person;
import com.petstore.User;
import nl.jqno.equalsverifier.EqualsVerifier;

public class ReflectReadableMappedMethodTest {

    @Test
    public void testHashCodeEquals() throws NoSuchMethodException, SecurityException {
        ReflectReadableMappedMethod<Object> field = getFirstNameField();
        EqualsVerifier.forClass(ReflectReadableMappedMethod.class)
        .withPrefabValues(Method.class, field.getGetter(), Person.class.getMethod("getLastName"))
        .verify();
    }

    @Test
    public void testReadValue() throws NoSuchMethodException, SecurityException {
        ReflectReadableMappedMethod<Object> field = getFirstNameField();
        Person person = new Person();
        person.setFirstName("foo");
        assertEquals("foo", field.readValue(person));
    }

    @Test
    public void testIsDeclaredBy() throws NoSuchMethodException, SecurityException {
        ReflectReadableMappedMethod<Object> field = getFirstNameField();
        assertTrue(field.isDeclaredBy(Person.class));
        assertFalse(field.isDeclaredBy(User.class));
    }
    
    @Test
    public void testReadValueError() throws NoSuchMethodException, SecurityException {
        Assertions.assertThrows(()->getFirstNameField().readValue(new User()));
    }

    private ReflectReadableMappedMethod<Object> getFirstNameField() throws NoSuchMethodException, SecurityException  {
        return ReflectReadableMappedMethod.of(Person.class.getMethod("getFirstName"));
    }
    
}
