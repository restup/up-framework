package com.github.restup.controller;

import static com.github.restup.controller.model.MediaType.APPLICATION_JSON_API;

import com.github.restup.controller.ResourceController.Builder;
import com.github.restup.controller.mock.AbstractMockTest;
import com.github.restup.test.RestApiAssertions;
import com.university.Course;
import com.university.Student;
import com.university.University;
import org.junit.Before;
import org.junit.Test;

public class CourseServiceMediaTypeTest extends AbstractMockTest {

    private final static String PARAM = "mt";

    public CourseServiceMediaTypeTest() {
        super("/courses", Course.class
            , Student.class
            , University.class);
    }

    @Before
    public void setup() {
        super.before();
        loader().relativeTo(CourseServiceTest.class)
            .load("course");
    }

    @Override
    protected Builder configureResourceController(Builder b) {
        return b.mediaTypeParam(PARAM);
    }

    @Override
    protected RestApiAssertions.Builder configureRestApiAssertions(RestApiAssertions.Builder b) {
        return b.jsonapi()
            .decorator((request) -> request.requestHeader("Content-Type", "text/html"));
    }

    @Test
    public void getCourse() {
        api.get(2)
            .param(PARAM, APPLICATION_JSON_API.getContentType())
            .ok();
    }

}
