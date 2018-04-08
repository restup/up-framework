package com.github.restup.test;

import static com.github.restup.test.ContentsAssertions.assertJson;
import static com.github.restup.test.ContentsAssertions.assertText;
import static com.github.restup.test.assertions.Assertions.assertPrivateConstructor;
import static com.github.restup.test.resource.RelativeTestResource.getCallingMethodName;
import static com.github.restup.test.resource.RelativeTestResource.getClassFromStack;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.restup.test.resource.Contents;
import com.github.restup.test.resource.RelativeTestResource;
import com.google.gson.Gson;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RelativeTestResource.class)
public class ContentAssertionsTest {

    @Test
    public void testPrivateConstructor() {
        assertPrivateConstructor(ContentsAssertions.class);
    }

    @Test
    public void testString() {
        assertJson()
                .expect("{}")
                .matches("{}");
    }

    @Test
    public void testObjectMapper() {
        assertJson(new ObjectMapper())
                .expect(Arrays.asList(1, 2, 3))
                .matches(Arrays.asList(1, 2, 3));
    }

    @Test
    public void testObject() {
        assertJson(new Gson())
                .expect(Arrays.asList(1, 2, 3))
                .matches(Arrays.asList(1, 2, 3));
    }

    @Test
    public void testBytes() {
        assertText()
                .expect("foo".getBytes())
                .matches("foo".getBytes());
    }

    @Test
    public void testContents() {
        assertText(getClass())
            .expect(Contents.of("foo"))
            .matches(Contents.of("foo"));
    }

    @Test
    public void testRelativeContents() {
        // have to use power mock since this test class is in com.github.restup and testing which follows
        // stack looks for first non com.github.restup class
        PowerMockito.mockStatic(RelativeTestResource.class);
        when(getClassFromStack()).thenReturn((Class)ContentAssertionsTest.class);
        when(getCallingMethodName()).thenReturn("testRelativeContents");

        assertText().matches("foo");
    }

}
