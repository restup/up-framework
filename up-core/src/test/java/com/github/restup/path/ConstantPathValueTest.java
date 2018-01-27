package com.github.restup.path;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Map;
import org.junit.Test;
import com.github.restup.service.model.ResourceData;
import com.github.restup.test.assertions.Assertions;

public class ConstantPathValueTest {

    @Test
    public void testHashCodeEquals() {
        Assertions.assertHashCodeEquals(ConstantPathValue.class, "field");
    }
    
    @Test
    public void testPojo() {
        Assertions.pojo(ConstantPathValue.class);
    }

    @Test
    public void testReservedPath() {
        assertTrue(PathValue.data().isReservedPath());
    }

    @Test
    public void testSupportsType() {
        assertFalse(PathValue.invalid("foo").supportsType(Map.class));
        assertTrue(PathValue.data().supportsType(ResourceData.class));
    }
}
