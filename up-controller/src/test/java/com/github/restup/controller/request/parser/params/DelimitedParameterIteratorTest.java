package com.github.restup.controller.request.parser.params;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.NoSuchElementException;
import org.junit.Test;

public class DelimitedParameterIteratorTest {

    @Test
    public void testNoDelimitedValues() {
        String[] values = new String[]{"a", "b", "c", "d"};
        DelimitedParameterIterator it = new DelimitedParameterIterator(",", values);
        for (String s : values) {
            assertTrue(it.hasNext());
            assertEquals(s, it.next());
        }
        assertFalse(it.hasNext());
    }

    @Test
    public void testSimpleDelimitedValues() {
        String[] values = new String[]{"a", "b", "c", "d", "e", "f", "g"};
        DelimitedParameterIterator it = new DelimitedParameterIterator(",", "a,b",
            "c,d,e,f",
            "g");
        for (String s : values) {
            assertTrue(it.hasNext());
            assertEquals(s, it.next());
        }
        assertFalse(it.hasNext());
    }

    @Test
    public void testNullAndEmptyDelimitedValues() {
        String[] values = new String[]{"a", "b", "c", " d", "e ", "f", "g"};
        DelimitedParameterIterator it = new DelimitedParameterIterator(",", "a,b",
            null, null, ",", null, ",",
            "c, d,e ,f",
            "g");
        for (String s : values) {
            assertTrue(it.hasNext());
            assertEquals(s, it.next());
        }
        assertFalse(it.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void testNull() {
        DelimitedParameterIterator it = new DelimitedParameterIterator(",", null);
        assertFalse(it.hasNext());
        it.next();
    }

    @Test(expected = NoSuchElementException.class)
    public void testEmpty() {
        DelimitedParameterIterator it = new DelimitedParameterIterator(",", ",");
        assertFalse(it.hasNext());
        it.next();
    }
}
