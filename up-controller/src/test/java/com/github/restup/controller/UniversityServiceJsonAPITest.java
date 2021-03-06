package com.github.restup.controller;

import com.github.restup.controller.mock.AbstractMockJsonApiTest;
import com.university.Course;
import com.university.Student;
import com.university.University;
import org.junit.Before;
import org.junit.Test;

public class UniversityServiceJsonAPITest extends AbstractMockJsonApiTest {

    public UniversityServiceJsonAPITest() {
        super(University.PLURAL_NAME, Course.class
                , Student.class
                , University.class);
    }

    @Before
    public void setup() {
        super.before();
        loader().relativeTo(CourseServiceTest.class).load("course");
    }

    @Test
    public void listPaged() {
        api.list().ok();
    }

}
