package com.github.restup.jackson;

import org.junit.Test;
import com.github.restup.test.assertions.Assertions;

public class JacksonConfigurationTest {

    @Test
    public void testPrivateConstructor() {
        Assertions.assertPrivateConstructor(JacksonConfiguration.class);
    }

}
