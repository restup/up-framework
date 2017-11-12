package com.github.restup.service;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class IndexedVarArgsMethodExecutorTest {

    @Test
    public void testExecute() throws NoSuchMethodException, SecurityException {
        LazyIndexedVarArgsMethodCommand executor = new LazyIndexedVarArgsMethodCommand(new Foo(), Foo.class.getMethod("bar", String.class));
        assertNull(executor.indexes);

        Object o = executor.execute(new Date(), 10, "Hello World");
        assertEquals("Hello World", o);
        assertEquals(Integer.valueOf(2), executor.indexes[0]);

        // execute second time with indexes
        o = executor.execute(new Date(), 10, "Hello Again");
        assertEquals("Hello Again", o);
    }

    public class Foo {
        public String bar(String message) {
            return message;
        }
    }
}
