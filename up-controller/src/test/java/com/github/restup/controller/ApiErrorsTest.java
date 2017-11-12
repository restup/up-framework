package com.github.restup.controller;

import com.github.restup.test.RestApiTest;
import org.junit.Test;

public class ApiErrorsTest extends AbstractMockTest {

    public ApiErrorsTest() {
        super("/universities", 1);
    }


    @Test
    public void error400BodyInvalid() {
        api.add("{\"data\":{\"foo\":1,\"bar\":1,\"name\":1}}").error400();
    }

    @Test
    public void error400BodyRequired() {
        api.add().error400();
        api.add("{}").error400();
    }

    @Test
    public void error400InvalidRelationship() {
        RestApiTest.Builder api = builder("/courses/{courseId}/universities", 5);
        api.get().error400();
    }

    @Test
    public void error400JavaxNotBlank() {
        api.add("{\"data\":{}}").error400();
    }

    @Test
    public void error403IdNotAllowed() {
        api.add("{\"data\":{\"id\":1,\"name\":\"foo\"}}").error403();
    }

    @Test
    public void error404UnknownResource() {
        RestApiTest.Builder api = builder("/foos", 5);
        api.get().error404();
    }

}
