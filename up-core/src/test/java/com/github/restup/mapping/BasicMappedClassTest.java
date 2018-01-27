package com.github.restup.mapping;

import org.junit.Test;
import com.github.restup.test.assertions.Assertions;

public class BasicMappedClassTest {

    @Test
    public void testHashCodeEquals() {
        Assertions.assertHashCodeEquals(BasicMappedClass.class, "type", "name");
    }

}
