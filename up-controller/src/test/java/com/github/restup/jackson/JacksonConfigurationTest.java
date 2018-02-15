package com.github.restup.jackson;

import org.junit.Test;
import com.github.restup.test.assertions.Assertions;

public class JacksonConfigurationTest {

    @Test
    public void testtPrivateConstructor() {
        Assertions.assertPrivateConstructor(JacksonConfiguration.class);
    }

}
