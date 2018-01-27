package com.github.restup.registry.settings;

import org.junit.Test;
import com.github.restup.test.assertions.Assertions;

public class PojoTest {
    
    @Test
    public void testPrivateConstructor() {
        Assertions.assertPrivateConstructor(AutoDetectConstants.class);
    }

    @Test
    public void testPojo() {
        Assertions.pojo(ControllerMethodAccess.class
                , ServiceMethodAccess.class);
    }
}
