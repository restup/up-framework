package com.github.restup.test.assertions;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;

/**
 * Provides convenience methods for testing using various open source projects using sensible defaults
 * or offering more concise test methods.
 */
public class Assertions {

    private Assertions() {
        super();
    }

    /**
     * Assert that the classes have a private constructor
     * @param classes to test
     */
    public static void assertPrivateConstructor(Class<?>... classes) {
        for ( Class<?> clazz : classes ) {
            try {
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                assertThat(Modifier.isPrivate(constructor.getModifiers()), is(true));
                constructor.setAccessible(true);
                constructor.newInstance();
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new AssertionError("Private constructor does not exist");
            }
        }
    }

    /**
     * Convenience method to use assertJ to assert that the an instance of e is thrown by f.
     *
     * <p>Ex. {@code assertThrows( () -> foo(), NullPointerException.class) }</p>
     * @param f callable that is expected to throw excpetion e
     * @param e expected exception
     * @param <E> exception type
     * @return AbstractThrowableAssert which may be used for additional asserj assertions
     */
    public static <E extends Throwable> AbstractThrowableAssert<?, ?> assertThrows(ThrowingCallable f, Class<E> e) {
        Throwable thrownException = catchThrowable(f);

        return org.assertj.core.api.Assertions.assertThat(thrownException)
            .isInstanceOf(e);
    }

    /**
     * Creates {@link PojoAssertions} to validate pojos using open pojo.
     * <b>Must call validate()</b>
     * <p>Ex. {@code pojo(MyClass.class).validate()}</p>
     * @param classes to test
     * @return a PojoAssertions builder
     */
    public static PojoAssertions pojo(Class<?>... classes) {
        return new PojoAssertions().add(classes);
    }

    /**
     * Convenience method to verify hashCode and equals on a class using {@link EqualsVerifier}
     * using specified fields
     * @param clazz to test
     * @param usingFields to be passed to {@link EqualsVerifier#withOnlyTheseFields(String...)}
     */
    public static void assertHashCodeEquals(Class<?> clazz, String... usingFields) {
        EqualsVerifier.forClass(clazz)
        .withOnlyTheseFields(usingFields)
        .verify();
    }

    /**
     * Convenience method to verify hashCode and equals on a class using {@link EqualsVerifier}
     * @param clazz to test
     */
    public static void assertHashCodeEquals(Class<?> clazz) {
        EqualsVerifier.forClass(clazz)
            .verify();
    }

    /**
     * Convenience method to verify hashCode and equals on multiple classes using {@link EqualsVerifier}
     * @param classes to test
     */
    public static void assertHashCodeEquals(Class<?>... classes) {
        for (Class<?> clazz : classes ) {
            assertHashCodeEquals(clazz);
        }
    }

}
