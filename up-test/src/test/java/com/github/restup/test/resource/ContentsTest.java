package com.github.restup.test.resource;

import org.junit.Test;
import static org.junit.Assert.*;
import com.github.restup.test.resource.Contents.Builder;
import static com.github.restup.test.resource.Contents.*;

public class ContentsTest {
    

    @Test
    public void testContentsBuilderString() {
        assertContents("foo", builder().contents("foo"));
    }
    
    @Test
    public void testContentsBuilderBytes() {
        assertContents("bytes", builder().contents("bytes".getBytes()));
    }
    
    @Test
    public void testContentsBuilderContents() {
        assertContents("contents", builder().contents(Contents.of("contents")));
    }
    
    @Test
    public void testContentsBuilderRelativeResource() {
        assertContents("file", builder().testClass(getClass()).testName("contents"));
    }

    private void assertContents(String expected, Builder builder) {
        assertEquals(expected, builder.build().getContentAsString());
    }

}
