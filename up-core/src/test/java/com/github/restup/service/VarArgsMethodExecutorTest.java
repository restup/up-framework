package com.github.restup.service;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import org.junit.Test;

public class VarArgsMethodExecutorTest {

    /**
     * Test method is executed with correct argument selected from  var args of different types
     */
    @Test
    public void testExecute() throws NoSuchMethodException, SecurityException {
        VarArgsMethodCommand executor = new VarArgsMethodCommand(new Foo(), Foo.class.getMethod("bar", String.class));
        Object o = executor.execute(new Date(), 10, "Hello World");
        assertEquals("Hello World", o);
    }

    public class Foo {

        public String bar(String message) {
            return message;
        }
    }

}
