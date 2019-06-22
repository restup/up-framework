package com.github.restup.controller;

import com.github.restup.controller.mock.AbstractMockTest;
import com.github.restup.controller.mock.ApiTest;
import com.github.restup.test.MediaType;
import com.university.Course;
import com.university.Student;
import com.university.University;
import org.junit.Before;

abstract class AbstractMediaTypeTest extends AbstractMockTest {

    final static String PARAM = "mt";

    public AbstractMediaTypeTest(String path) {
        super(ApiTest.builder(Course.class
            , Student.class
            , University.class)
            .decorateController(b -> b.mediaTypeParam(PARAM))
            .decorateApi(b -> b.jsonapi().mediaType(MediaType.APPLICATION_JSON_API)
                .requestHeader("Content-Type", "text/html")), path, 1);
    }

    @Before
    public void setup() {
        loader().relativeTo(CourseServiceTest.class)
            .load("course");
    }

}
