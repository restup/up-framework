package com.github.restup.test.serializer;

import static com.github.restup.test.assertions.Assertions.assertPrivateConstructor;
import static org.junit.Assert.*;
import org.junit.Test;

public class AutoDetectConstantsTest {

    @Test
    public void testConstants() {
        assertTrue(AutoDetectConstants.GSON_EXISTS);
        assertTrue(AutoDetectConstants.JACKSON2_EXISTS);
    }
    
    @Test
    public void testPrivateConstructor() {
        assertPrivateConstructor(AutoDetectConstants.class);
    }
    
    @Test
    public void testClassesExist() {
        assertFalse(AutoDetectConstants.classesExist("foo.bar.super.bowl"));
    }
    
}
