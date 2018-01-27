package com.github.restup.test.assertions;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import nl.jqno.equalsverifier.EqualsVerifier;

public class Assertions {

    public static <T> void assertPrivateConstructor(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            assertThat(Modifier.isPrivate(constructor.getModifiers()), is(true));
            constructor.setAccessible(true);
            constructor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new AssertionError("Private constructor does not exist");
        }
    }

    public static <E extends Throwable> AbstractThrowableAssert<?, ?> assertThrows(ThrowingCallable f, Class<E> e) {
        Throwable thrownException = catchThrowable(f);
        
        return org.assertj.core.api.Assertions.assertThat(thrownException)
                .isInstanceOf(e);
    }
    
    public static PojoAssertions pojo(Class<?>... classes) {
        return new PojoAssertions().add(classes);
    }

    public static void assertHashCodeEquals(Class<?> clazz, String... usingFields) {
        EqualsVerifier.forClass(clazz)
        .withOnlyTheseFields(usingFields)
        .verify();
    }

    public static void assertHashCodeEquals(Class<?> clazz) {
        EqualsVerifier.forClass(clazz)
        .verify();
    }
    
    private Assertions() {
        super();
    }

}
