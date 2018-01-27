package com.github.restup.test.resource;

import org.junit.Test;
import com.github.restup.test.assertions.Assertions;

public class RelativeTestResourceTest {

    @Test
    public void testDump() {
        Assertions.assertThrows(() -> 
            RelativeTestResource.dump(RelativeTestResourceTest.class, "foo")
                .getContentAsByteArray(),
                AssertionError.class);
    }

}
