package com.github.restup.test;

import static com.github.restup.test.assertions.Assertions.assertPrivateConstructor;
import org.junit.Test;

public class RestApiAssertionsTest {

    @Test
    public void testPrivateConstructor() {
        assertPrivateConstructor(RestApiAssertions.class);
    }
}
