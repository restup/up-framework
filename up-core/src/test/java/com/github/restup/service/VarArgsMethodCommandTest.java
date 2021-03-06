package com.github.restup.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import org.junit.Test;
import com.github.restup.assertions.Assertions;
import com.github.restup.errors.RequestErrorException;

public class VarArgsMethodCommandTest {


    @Test
    public void testInvocationException() throws NoSuchMethodException, SecurityException {
        VarArgsMethodCommand executor = new VarArgsMethodCommand(new Foo(), Foo.class.getMethod("invocation"));
        Assertions.assertThrows(() -> executor.execute());
    }

    @Test
    public void testRequestErrorException() throws NoSuchMethodException, SecurityException {
        VarArgsMethodCommand executor = new VarArgsMethodCommand(new Foo(), Foo.class.getMethod("errorObject"));
        Assertions.assertThrows(() -> executor.execute());
    }

    @Test
    public void testHandle() throws NoSuchMethodException, SecurityException {
        RequestErrorException requestErrorException = new RequestErrorException(new IllegalArgumentException());
        VarArgsMethodCommand executor = new VarArgsMethodCommand(new Foo(), Foo.class.getMethod("errorObject"));
        assertSame(requestErrorException, executor.handle(new RuntimeException(requestErrorException)));
    }

    /**
     * Test method is executed with correct argument selected from var args of different types
     */
    @Test
    public void testExecute() throws NoSuchMethodException, SecurityException {
        VarArgsMethodCommand executor = new VarArgsMethodCommand(new Foo(), Foo.class.getMethod("bar", String.class));
        Object o = executor.execute(new Date(), 10, "Hello World");
        assertEquals("Hello World", o);
    }

    public class Foo {
        // bummer, seems powermock can't mock Method
        // https://stackoverflow.com/questions/9928432/how-do-i-mock-java-lang-reflect-method-class-in-powermockito
        public String bar(String message) {
            return message;
        }

        public void invocation() throws InvocationTargetException {
            throw new IllegalArgumentException();
        }

        public void errorObject() throws InvocationTargetException {
            throw RequestErrorException.of(new IllegalArgumentException());
        }
    }

}
