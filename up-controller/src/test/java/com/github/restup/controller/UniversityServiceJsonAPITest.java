package com.github.restup.controller;

import com.university.University;
import org.junit.Before;
import org.junit.Test;


public class UniversityServiceJsonAPITest extends AbstractMockTest {

    public UniversityServiceJsonAPITest() {
        super(University.PLURAL_NAME, 1);
        jsonapi();
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
