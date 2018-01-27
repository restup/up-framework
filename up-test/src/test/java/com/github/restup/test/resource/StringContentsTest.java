package com.github.restup.test.resource;

import org.junit.Test;

import static org.junit.Assert.*;

public class StringContentsTest {

    @Test
    public void testStringContents() {
        StringContents contents = new StringContents("foo");
        assertEquals("foo", contents.getContentAsString());
        assertEquals("foo", new String(contents.getContentAsByteArray()));
    }
}
