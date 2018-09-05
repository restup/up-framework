package com.github.restup.controller;

import static com.github.restup.controller.model.MediaType.APPLICATION_JSON_API;

import org.junit.Test;

public class DiscoveryServiceMediaTypeTest extends AbstractMediaTypeTest {

    public DiscoveryServiceMediaTypeTest() {
        super("/");
    }

    @Test
    public void getResources() {
        api.list()
            .param(PARAM, APPLICATION_JSON_API.getContentType())
            .ok();
    }

}
