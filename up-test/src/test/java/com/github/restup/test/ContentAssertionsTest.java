package com.github.restup.test;

import static com.github.restup.test.assertions.Assertions.assertPrivateConstructor;
import java.util.Arrays;
import org.junit.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.restup.test.resource.Contents;
import com.google.gson.Gson;

public class ContentAssertionsTest {

    @Test
    public void testPrivateConstructor() {
        assertPrivateConstructor(ContentsAssertions.class);
    }

    @Test
    public void testString() {
        ContentsAssertions.json()
                .expect("{}")
                .matches("{}");
    }

    @Test
    public void testObjectMapper() {
        ContentsAssertions.json(new ObjectMapper())
                .expect(Arrays.asList(1, 2, 3))
                .matches(Arrays.asList(1, 2, 3));
    }

    @Test
    public void testObject() {
        ContentsAssertions.json(new Gson())
                .expect(Arrays.asList(1, 2, 3))
                .matches(Arrays.asList(1, 2, 3));
    }

    @Test
    public void testBytes() {
        ContentsAssertions.builder()
                .expect("foo".getBytes())
                .matches("foo".getBytes());
    }

    @Test
    public void testContents() {
        ContentsAssertions.builder(getClass())
                .expect(Contents.of("foo"))
                .matches(Contents.of("foo"));
    }

}
