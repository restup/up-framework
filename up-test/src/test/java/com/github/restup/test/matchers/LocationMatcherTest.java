package com.github.restup.test.matchers;

import static com.github.restup.test.matchers.LocationMatcher.matchesLocationPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

import org.junit.Test;

public class LocationMatcherTest {


    @Test
    public void testLocationMatcher() {
        assertThat("http://localhost/messages/8caf83a5-5518-4e08-9b4d-857606c83d3f",
            matchesLocationPath("/messages"));
        assertThat("http://localhost/foo/1", matchesLocationPath("foo"));
        assertThat("https://localhost/foo/1", matchesLocationPath("foo"));
        assertThat("https://localhost/some/path/foo/1", matchesLocationPath("foo"));
        assertThat("ftp://localhost/foo/1", not(matchesLocationPath("foo")));
        assertThat("http://localhost/foo/1/2", not(matchesLocationPath("foo")));
        assertThat("http://localhost/bar/1", not(matchesLocationPath("foo")));
    }
}
