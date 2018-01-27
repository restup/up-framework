package com.github.restup.mapping.fields.composition;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.lang.reflect.Field;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import com.github.restup.assertions.Assertions;
import com.model.test.company.Person;
import com.petstore.User;
import nl.jqno.equalsverifier.EqualsVerifier;

public class ReflectMappedFieldTest {

    @Test
    public void testHashCodeEquals() throws NoSuchFieldException, SecurityException {
        EqualsVerifier.forClass(ReflectMappedField.class)
        .withPrefabValues(Field.class, getFirstNameField().getField(), Person.class.getDeclaredField("lastName"))
        .verify();
    }
    
    @Test
    public void testReadWriteValue() throws NoSuchFieldException, SecurityException {
        ReflectMappedField<Person,Object> field = getFirstNameField();
        Person person = new Person();
        field.writeValue(person, "foo");
        assertEquals("foo", field.readValue(person));
    }

    @Test
    public void testIsDeclaredBy() throws NoSuchFieldException, SecurityException {
        ReflectMappedField<Person,Object> field = getFirstNameField();
        assertTrue(field.isDeclaredBy(Person.class));
        assertFalse(field.isDeclaredBy(User.class));
    }
    
    @Test
    public void testWriteValueError() throws NoSuchFieldException, SecurityException {
        Assertions.assertThrows(()->getFirstNameField().writeValue(new Person(), 1l));
    }
    
    @Test
    public void testReadValueError() throws NoSuchFieldException, SecurityException {
        Assertions.assertThrows(()->getFirstNameField().readValue(new User()));
    }
    
    @Test
    public void testCreateDeclaringInstance() throws NoSuchFieldException, SecurityException {
        MatcherAssert.assertThat(getFirstNameField().createDeclaringInstance(), instanceOf(Person.class));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private ReflectMappedField<Person,Object> getFirstNameField() throws NoSuchFieldException, SecurityException {
        return (ReflectMappedField) ReflectMappedField.of(Person.class.getDeclaredField("firstName"));
    }

}
