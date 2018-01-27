package com.github.restup.test.assertions;

import static com.github.restup.test.assertions.Assertions.assertPrivateConstructor;
import static com.github.restup.test.assertions.Assertions.assertThrows;
import org.junit.Test;
import com.github.restup.test.model.Foo;

public class AssertionsTest {

    @Test
    public void testPrivateConstructor() {
        assertPrivateConstructor(Assertions.class);
    }

    @Test
    public void testPrivateConstructorException() {
        assertThrows(()->assertPrivateConstructor(ExceptionOnCreate.class),AssertionError.class);
    }

    @Test
    public void testNoPrivateConstructor() {
        assertThrows(()->assertPrivateConstructor(Foo.class), AssertionError.class);
    }
    
    private static class ExceptionOnCreate {
        private ExceptionOnCreate() {
            throw new IllegalStateException();
        }
    }
}
