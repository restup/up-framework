package com.github.restup.mapping.fields.composition;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.*;
import java.lang.reflect.Method;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import com.github.restup.assertions.Assertions;
import com.model.test.company.Person;
import com.petstore.User;
import nl.jqno.equalsverifier.EqualsVerifier;

public class ReflectMappedMethodTest {

    @Test
    public void testHashCodeEquals() throws NoSuchMethodException, SecurityException {
        ReflectMappedMethod<Person,Object> method = getFirstNameField();
        EqualsVerifier.forClass(ReflectMappedMethod.class)
        .withPrefabValues(Method.class, method.getGetter(), method.getSetter())
        .withRedefinedSuperclass()
        .verify();
    }

    @Test
    public void testReadWriteValue() throws NoSuchMethodException, SecurityException {
        ReflectMappedMethod<Person,Object> field = getFirstNameField();
        Person person = new Person();
        field.writeValue(person, "foo");
        assertEquals("foo", field.readValue(person));
    }

    @Test
    public void testIsDeclaredBy() throws NoSuchMethodException, SecurityException {
        ReflectMappedMethod<Person,Object> field = getFirstNameField();
        assertTrue(field.isDeclaredBy(Person.class));
        assertFalse(field.isDeclaredBy(User.class));
    }
    
    @Test
    public void testWriteValueError() throws NoSuchMethodException, SecurityException {
        Assertions.assertThrows(()->getFirstNameField().writeValue(new Person(), 1l));
    }
    
    @Test
    public void testReadValueError() throws NoSuchMethodException, SecurityException {
        Assertions.assertThrows(()->getFirstNameField().readValue(new User()));
    }
    
    @Test
    public void testCreateDeclaringInstance() throws NoSuchMethodException, SecurityException {
        MatcherAssert.assertThat(getFirstNameField().createDeclaringInstance(), instanceOf(Person.class));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private ReflectMappedMethod<Person,Object> getFirstNameField() throws NoSuchMethodException, SecurityException  {
        return (ReflectMappedMethod) ReflectMappedMethod.of(Person.class.getMethod("getFirstName"), Person.class.getMethod("setFirstName",String.class));
    }
    
}
