package com.github.restup.test.resource;

import static com.github.restup.test.resource.Contents.builder;
import static org.junit.Assert.assertEquals;

import com.github.restup.test.resource.Contents.Builder;
import org.junit.Test;

public class ContentsTest {
    

    @Test
    public void testContentsBuilderString() {
        this.assertContents("foo", builder().contents("foo"));
    }
    
    @Test
    public void testContentsBuilderBytes() {
        this.assertContents("bytes", builder().contents("bytes".getBytes()));
    }
    
    @Test
    public void testContentsBuilderContents() {
        this.assertContents("contents", builder().contents(Contents.of("contents")));
    }
    
    @Test
    public void testContentsBuilderRelativeResource() {
        this.assertContents("file", builder().relativeTo(this.getClass()).name("contents"));
    }

    private void assertContents(String expected, Builder builder) {
        assertEquals(expected, builder.build().getContentAsString());
    }

}
