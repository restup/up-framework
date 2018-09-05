package com.github.restup.controller;

import com.github.restup.controller.ResourceController.Builder;
import com.github.restup.controller.mock.AbstractMockTest;
import com.github.restup.test.RestApiAssertions;
import com.university.Course;
import com.university.Student;
import com.university.University;
import org.junit.Before;

abstract class AbstractMediaTypeTest extends AbstractMockTest {

    final static String PARAM = "mt";

    public AbstractMediaTypeTest(String path) {
        super(path, Course.class
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


}
