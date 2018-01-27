package com.github.restup.mapping.fields.composition;

import org.junit.Test;
import com.github.restup.test.assertions.Assertions;

public class BasicCaseSensitivityTest {

    @Test
    public void testHashCodeEquals() {
        Assertions.assertHashCodeEquals(BasicCaseSensitivity.class);
    }

}
