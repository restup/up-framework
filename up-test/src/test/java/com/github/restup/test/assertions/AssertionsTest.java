package com.github.restup.test.assertions;

import static com.github.restup.test.assertions.Assertions.assertHashCodeEquals;
import static com.github.restup.test.assertions.Assertions.assertPrivateConstructor;
import static com.github.restup.test.assertions.Assertions.assertThrows;

import java.util.Objects;
import org.junit.Test;

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

    @Test
    public void testHashCodeEquals() {
        assertHashCodeEquals(Foo.class);
        assertHashCodeEquals(Bar.class, "name");
    }

    private static class ExceptionOnCreate {
        private ExceptionOnCreate() {
            throw new IllegalStateException();
        }
    }

    private class Foo {
        private final String name;

        Foo(String name) {
            super();
            this.name = name;
        }

        String getName() {
            return this.name;
        }

        @Override
        public final int hashCode() {
            return Objects.hash(this.name);
        }

        @Override
        public final boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof Foo)) {
                return false;
            }
            Foo other = (Foo) obj;
            return Objects.equals(this.name, other.name);
        }

        @Override
        public String toString() {
            return "Foo{" +
                "name='" + this.name + '\'' +
                '}';
        }
    }


    private class Bar {
        private final String name;
        private String description;

        Bar(String name) {
            super();
            this.name = name;
        }

        String getName() {
            return this.name;
        }

        @Override
        public final int hashCode() {
            return Objects.hash(this.name);
        }

        @Override
        public final boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof Bar)) {
                return false;
            }
            Bar other = (Bar) obj;
            return Objects.equals(this.name, other.name);
        }

    }
}
