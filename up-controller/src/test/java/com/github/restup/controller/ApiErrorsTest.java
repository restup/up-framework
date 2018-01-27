package com.github.restup.controller;

import com.github.restup.test.RestApiAssertions;
import com.university.Course;
import com.university.Student;
import com.university.University;

import org.junit.Test;

public class ApiErrorsTest extends AbstractMockTest {

    public ApiErrorsTest() {
        super("/universities", Course.class
                , Student.class
                , University.class);
    }

    @Test
    public void error400BodyInvalid() {
        api.add("{\"data\":{\"foo\":1,\"bar\":1,\"name\":1}}").error400();
    }

    @Test
    public void error400BodyRequired() {
        api.add().body((String)null).error400();
        api.add("{}").error400();
    }

    @Test
    public void error400InvalidRelationship() {
        RestApiAssertions.Builder api = builder("/courses/{courseId}/universities", 5);
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
        RestApiAssertions.Builder api = builder("/foos", 5);
        api.get().error404();
    }

}
