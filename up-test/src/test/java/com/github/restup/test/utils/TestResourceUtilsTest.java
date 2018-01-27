package com.github.restup.test.utils;

import static com.github.restup.test.assertions.Assertions.assertPrivateConstructor;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class TestResourceUtilsTest {

    @Test
    public void testPrivateConstructor() {
        assertPrivateConstructor(TestResourceUtils.class);
    }
    
    @Test
    public void testGetResource() {
        assertNotNull(
        TestResourceUtils.getResource("/com/github/restup/test/resource/ContentsTest/contents.txt")
        );
    }
    
}
