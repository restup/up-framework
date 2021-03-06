package com.github.restup.util;

import static com.github.restup.util.UpUtils.addAll;
import static com.github.restup.util.UpUtils.getFirst;
import static com.github.restup.util.UpUtils.names;
import static com.github.restup.util.UpUtils.put;
import static com.github.restup.util.UpUtils.removeAll;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.github.restup.annotations.field.RelationshipType;
import com.github.restup.test.assertions.Assertions;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class UpUtilsTest {
    
    @Test
    public void testPrivateConstructor() {
        Assertions.assertPrivateConstructor(UpUtils.class);
    }

    @Test
    public void testRemoveAll() {
        removeAll(Collections.emptyList(),null);
        removeAll(Collections.emptyList(),Collections.emptyList());
    }

    @Test
    public void testAddAll() {
        addAll(Collections.emptyList(),null);
        addAll(Collections.emptyList(),Collections.emptyList());
    }

    @Test
    public void testGetFirst() {
        assertNull(getFirst(Collections.emptyList()));
        assertNull(getFirst(null));
    }

    @Test
    public void testPut() {
        Map<String,String[]> map = new HashMap<>();
        put(map, "foo", "bar");
        assertThat(map.get("foo"), equalTo(new String[] {"bar"}));
        put(map, "foo", "baz");
        assertThat(map.get("foo"), equalTo(new String[] {"bar","baz"}));
    }
//    TODO
//    @Test
//    public void testHashCodeEquals() throws NoSuchFieldException, SecurityException, NoSuchMethodException {
//        EqualsVerifier.forClass(PropertyDescriptor.class)
//        .withOnlyTheseFields("name")
//        .withPrefabValues(Field.class, Person.class.getDeclaredField("lastName"), Person.class.getDeclaredField("firstName"))
//        .withPrefabValues(Method.class, Person.class.getMethod("getLastName"), Person.class.getMethod("getFirstName"))
//        .usingGetClass()
//        .verify();
//    }

    @Test
    public void testNames() {
        String[] names = names(RelationshipType.class);
        assertEquals("oneToOne", names[0]);
        assertEquals("oneToMany", names[1]);
        assertEquals("manyToOne", names[2]);
        assertEquals("manyToMany", names[3]);
        assertEquals(RelationshipType.values().length, names.length);
    }

    @Test
    public void testNamesFormatted() {
        String[] names = names("{0}_", RelationshipType.class);
        assertEquals("oneToOne_", names[0]);
        assertEquals("oneToMany_", names[1]);
        assertEquals("manyToOne_", names[2]);
        assertEquals("manyToMany_", names[3]);
        assertEquals(RelationshipType.values().length, names.length);

        names = names("[{0}]", RelationshipType.class);
        assertEquals("[oneToOne]", names[0]);
        assertEquals("[oneToMany]", names[1]);
        assertEquals("[manyToOne]", names[2]);
        assertEquals("[manyToMany]", names[3]);
        assertEquals(RelationshipType.values().length, names.length);
    }

}
