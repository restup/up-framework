package com.github.restup.mapping.fields.composition;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import com.github.restup.test.assertions.Assertions;
import com.model.test.company.Person;
import com.petstore.User;

public class MapFieldTest {

    @Test
    public void testHashCodeEquals() throws NoSuchFieldException, SecurityException {
        Assertions.assertHashCodeEquals(MapField.class);
    }
    
    @Test
    public void testReadWriteValue() throws NoSuchFieldException, SecurityException {
        MapField<Object> field = getFirstNameField();
        Map<String, Object> map = new HashMap<>();
        field.writeValue(map, "foo");
        assertEquals("foo", field.readValue(map));
    }

    @Test
    public void testIsDeclaredBy() throws NoSuchFieldException, SecurityException {
        MapField<Object> field = getFirstNameField();
        assertTrue(field.isDeclaredBy(Map.class));
        assertFalse(field.isDeclaredBy(User.class));
    }
    
    @Test
    public void testReadValueError() throws NoSuchFieldException, SecurityException {
        Assertions.assertThrows(()->getFirstNameField().readValue(new Person()), IllegalArgumentException.class);
    }
    
    @Test
    public void testCreateDeclaringInstance() throws NoSuchFieldException, SecurityException {
        MatcherAssert.assertThat(getFirstNameField().createDeclaringInstance(), instanceOf(Map.class));
    }
    
    @Test
    public void testCreateInstance() throws NoSuchFieldException, SecurityException {
        MatcherAssert.assertThat(getFirstNameField().createInstance(), instanceOf(Map.class));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private MapField<Object> getFirstNameField() throws NoSuchFieldException, SecurityException {
        return (MapField) MapField.of("firstName");
    }

}
