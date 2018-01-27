package com.github.restup.errors;

import org.junit.Test;
import static org.junit.Assert.*;

public class DefaultParameterErrorTest {
    
    @Test
    public void testError() {
        BasicParameterError err = new BasicParameterError("foo", "bar");
        assertEquals(err.getParameterName(), err.getSource());
    }

}
