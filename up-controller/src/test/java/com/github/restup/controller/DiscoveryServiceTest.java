package com.github.restup.controller;

import org.junit.Test;

import com.university.Course;
import com.university.Student;
import com.university.University;

public class DiscoveryServiceTest extends AbstractMockTest {

    public DiscoveryServiceTest() {
        super("/", Course.class
                , Student.class
                , University.class);
    }

    @Test
    public void getResources() {
        api.list().ok();
    }

}
