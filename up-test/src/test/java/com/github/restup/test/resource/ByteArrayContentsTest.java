package com.github.restup.test.resource;

import org.junit.Test;

import static org.junit.Assert.*;

public class ByteArrayContentsTest {

    @Test
    public void testByteArrayContents() {
        ByteArrayContents contents = new ByteArrayContents("foo".getBytes());
        assertEquals("foo", contents.getContentAsString());
        assertEquals("foo", new String(contents.getContentAsByteArray()));
    }
}
