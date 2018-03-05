package com.github.restup.controller.model;

import static com.github.restup.controller.model.AbstractResourceControllerRequestBuilder.getPathFromBasePath;
import static org.junit.Assert.assertEquals;
import java.util.function.Consumer;
import org.junit.Test;

public class AbstractResourceControllerRequestBuilderTest {

    @Test
    public void testGetPathFromBasePath() {
        assertEquals("foo", getPathFromBasePath(null, "/foo"));
        assertEquals("foo", getPathFromBasePath("", "/foo"));
        assertEquals("foo", getPathFromBasePath("/", "/foo"));

        Consumer<String> testBasePath = basePath -> {
            assertEquals("foo", getPathFromBasePath(basePath, "/api/foo"));
            assertEquals("", getPathFromBasePath(basePath, "/api/"));
            assertEquals("", getPathFromBasePath(basePath, "/api"));
        };

        testBasePath.accept("/api");
        testBasePath.accept("/api/");

    }

}
