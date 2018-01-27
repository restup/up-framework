package com.github.restup.path;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.github.restup.service.model.response.ReadResult;

public class DataPathValueTest {

    @Test
    public void testReadValue() {
        assertEquals("foo", PathValue.data().readValue("foo"));
        assertEquals("foo", PathValue.data().readValue(ReadResult.of("foo")));
    }

}
