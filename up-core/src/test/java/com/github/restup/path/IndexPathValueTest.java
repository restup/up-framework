package com.github.restup.path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.junit.Test;
import com.github.restup.test.assertions.Assertions;

public class IndexPathValueTest {

    @Test
    public void testReadArray() {
        String[] arr = new String[]{"a", "b", "c"};
        assertEquals("a", index(arr, 0));
        assertEquals("b", index(arr, 1));
        assertEquals("c", index(arr, 2));
    }

    @Test
    public void testReadList() {
        List<String> l = Arrays.asList("a", "b", "c");
        assertEquals("a", index(l, 0));
        assertEquals("b", index(l, 1));
        assertEquals("c", index(l, 2));
    }

    @Test
    public void testWriteArray() {
        String[] arr = new String[]{"a", "b", "c"};
        assertEquals("a", index(arr, 0));
        assertEquals("x", write(arr, 1, "x"));
        assertEquals("c", index(arr, 2));
    }

    @Test
    public void testWriteList() {
        List<String> l = Arrays.asList("a", "b", "c");
        assertEquals("a", index(l, 0));
        assertEquals("x", write(l, 1, "x"));
        assertEquals("c", index(l, 2));
    }

    @Test
    public void testReadNull() {
        assertEquals(null, index(null, 0));
    }

    @Test
    public void testWriteNull() {
        assertEquals(null, write(null, 1, "x"));
    }

    @Test
    public void testIndexPathValue() {
        assertThat(new IndexPathValue(1).createDeclaringInstance(), instanceOf(ArrayList.class));
        assertThat(new IndexPathValue(HashSet.class, 1).createDeclaringInstance(), instanceOf(HashSet.class));
    }

    @Test
    public void testReadValue() {
        Assertions.assertThrows(() -> new IndexPathValue(0).readValue(""),
                IllegalArgumentException.class);
    }

    @Test
    public void testWriteValue() {
        Assertions.assertThrows(() -> new IndexPathValue(0).writeValue("", ""),
                IllegalArgumentException.class);
    }

    private Object index(Object o, int i) {
        IndexPathValue pv = new IndexPathValue(i);
        return pv.readValue(o);
    }

    private Object write(Object o, int i, Object value) {
        IndexPathValue pv = new IndexPathValue(i);
        pv.writeValue(o, value);
        return pv.readValue(o);
    }
    
    @Test
    public void testHashCodeEquals() {
        Assertions.assertHashCodeEquals(IndexPathValue.class);
    }

}
