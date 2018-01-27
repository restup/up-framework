package com.github.restup.util;

import org.junit.Test;
import com.github.restup.test.assertions.Assertions;

public class ReflectionUtilsTest {
    
    @Test
    public void testPrivateConstructor() {
        Assertions.assertPrivateConstructor(ReflectionUtils.class);
    }

}
