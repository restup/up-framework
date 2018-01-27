package com.github.restup.mapping.fields.composition;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.lang.reflect.Method;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import com.github.restup.assertions.Assertions;
import com.model.test.company.Person;
import com.petstore.User;
import nl.jqno.equalsverifier.EqualsVerifier;

public class ReflectWritableMappedMethodTest {

    @Test
    public void testHashCodeEquals() throws NoSuchMethodException, SecurityException {
        ReflectWritableMappedMethod<Person,Object> field = getFirstNameField();
        EqualsVerifier.forClass(ReflectWritableMappedMethod.class)
        .withPrefabValues(Method.class, field.getSetter(), Person.class.getMethod("setLastName",String.class))
        .usingGetClass()
            .verify();
    }

    @Test
    public void testWriteValue() throws NoSuchMethodException, SecurityException {
        ReflectWritableMappedMethod<Person,Object> field = getFirstNameField();
        Person person = new Person();
        field.writeValue(person, "foo");
        assertEquals("foo", person.getFirstName());
    }

    @Test
    public void testIsDeclaredBy() throws NoSuchMethodException, SecurityException {
        ReflectWritableMappedMethod<Person,Object> field = getFirstNameField();
        assertTrue(field.isDeclaredBy(Person.class));
        assertFalse(field.isDeclaredBy(User.class));
    }
    
    @Test
    public void testWriteValueError() throws NoSuchMethodException, SecurityException {
        Assertions.assertThrows(()->getFirstNameField().writeValue(new Person(), 1l));
    }
    
    @Test
    public void testCreateDeclaringInstance() throws NoSuchMethodException, SecurityException {
        MatcherAssert.assertThat(getFirstNameField().createDeclaringInstance(), instanceOf(Person.class));
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    private ReflectWritableMappedMethod<Person,Object> getFirstNameField() throws NoSuchMethodException, SecurityException  {
        return (ReflectWritableMappedMethod) ReflectWritableMappedMethod.of(Person.class.getMethod("setFirstName",String.class));
    }
    
}
