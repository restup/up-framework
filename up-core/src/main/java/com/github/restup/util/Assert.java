package com.github.restup.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * Spring framework style assertions
 *
 * @author andy.buttaro
 */
public class Assert {

    private Assert() {

    }

    /**
     * Assert that an object is not {@code null}.
     * <pre class="code">Assert.notNull(clazz, "The class must not be null");</pre>
     *
     * @param object  the object to check
     * @param message the exception message to use if the assertion fails
     * @throws AssertionError if the object is {@code null}
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new AssertionError(message);
        }
    }

    public static <T> void notEmpty(Collection<?> c, String message) {
        if (CollectionUtils.isEmpty(c)) {
            throw new AssertionError(message);
        }
    }

    public static void notEmpty(Map<?, ?> c, String message) {
        if (MapUtils.isEmpty(c)) {
            throw new AssertionError(message);
        }
    }

    public static <T> void notEmpty(String message, T... args) {
        if (args.length < 1) {
            throw new AssertionError(message);
        }
    }

    public static <T, ID extends Serializable> void isNull(Object object, String message) {
        if (object != null) {
            throw new AssertionError(message);
        }
    }

}
