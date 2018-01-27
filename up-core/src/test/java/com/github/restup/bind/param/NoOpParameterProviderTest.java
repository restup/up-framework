package com.github.restup.bind.param;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Collections;

public class NoOpParameterProviderTest {

    @Test
    public void testGetParameter() {
        assertNull(NoOpParameterProvider.getInstance().getParameter("foo"));
    }

    @Test
    public void testGetParameterNames() {
        assertEquals(Collections.emptyList(), NoOpParameterProvider.getInstance().getParameterNames());
    }
}
