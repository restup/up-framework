package com.github.restup;

import com.github.restup.test.resource.RelativeTestResource;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RelativeTestResourceTest {

    private final String BASE_PATH = System.getProperty("user.dir") + "/src/test/resources";

    @Test
    public void testResponse() {
        MatcherAssert.assertThat(RelativeTestResource.response("foo").getPath(),
                is(BASE_PATH + "/com/github/restup/RelativeTestResourceTest/responses/foo.json"));
    }

    @Test
    public void testRequest() {
        MatcherAssert.assertThat(RelativeTestResource.request("foo").getPath(),
                is(BASE_PATH + "/com/github/restup/RelativeTestResourceTest/requests/foo.json"));
    }

    @Test
    public void testDump() {
        MatcherAssert.assertThat(RelativeTestResource.dump("foo").getPath(),
                is(BASE_PATH + "/com/github/restup/RelativeTestResourceTest/dumps/foo.json"));
    }

    @Test
    public void testResult() {
        MatcherAssert.assertThat(RelativeTestResource.result("foo").getPath(),
                is(BASE_PATH + "/com/github/restup/RelativeTestResourceTest/results/foo.json"));
    }
}
