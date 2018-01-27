package com.github.restup.test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class MediaTypeTest {
    
    @Test
    public void testGetContentType() {
        assertEquals("application/json", MediaType.APPLICATION_JSON.getContentType());
    }

}
