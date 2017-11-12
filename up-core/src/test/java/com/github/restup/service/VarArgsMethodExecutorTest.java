package com.github.restup.service;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

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
