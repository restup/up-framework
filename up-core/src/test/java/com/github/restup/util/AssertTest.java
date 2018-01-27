package com.github.restup.util;

import static com.github.restup.util.Assert.isNull;
import static com.github.restup.util.Assert.notEmpty;
import static com.github.restup.util.Assert.notNull;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import org.junit.Test;
import com.github.restup.test.assertions.Assertions;

public class AssertTest {
    
    @Test
    public void testPrivateConstructor() {
        Assertions.assertPrivateConstructor(Assert.class);
    }
    
    private void test(BiConsumer<?,String> f) {
        Assertions.assertThrows(() -> f.accept(null,"foo"), AssertionError.class)
        .hasMessage("foo");
    }
    
    @Test
    public void testNotNull() {
        test((a, b) -> notNull(a,b));
    }
    
    @Test
    public void testNotEmptyList() {
        test((a, b) -> notEmpty((List<?>)a,b));
    }
    
    @Test
    public void testNotEmptyMap() {
        test((a, b) -> notEmpty((Map<?,?>)a,b));
    }
    
    @Test
    public void testNotEmptyString() {
        test((a, b) -> notEmpty((String)a,b));
    }
    
    @Test
    public void testIsNull() {
        test((a, b) -> isNull(1,b));
    }

    @Test
    public void testNotEmpty() {
        Assertions.assertThrows(() -> notEmpty("foo"), AssertionError.class)
        .hasMessage("foo");
    }

}
