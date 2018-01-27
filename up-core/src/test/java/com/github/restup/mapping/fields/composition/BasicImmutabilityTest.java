package com.github.restup.mapping.fields.composition;

import org.junit.Test;
import com.github.restup.test.assertions.Assertions;

public class BasicImmutabilityTest {
    
    @Test
    public void testHashCodeEquals() {
        Assertions.assertHashCodeEquals(BasicImmutability.class);
    }

}
