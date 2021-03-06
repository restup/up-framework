package com.github.restup.controller;

import static com.github.restup.controller.model.MediaType.APPLICATION_JSON_API;

import org.junit.Test;

public class CourseServiceMediaTypeTest extends AbstractMediaTypeTest {

    public CourseServiceMediaTypeTest() {
        super("/courses");
    }
    
    @Test
    public void getCourse() {
        api.get(2)
            .param(PARAM, APPLICATION_JSON_API.getContentType())
            .ok();
    }

}
