package com.github.restup.controller.mock;

public class AbstractMockJsonApiTest extends AbstractMockTest {

    protected AbstractMockJsonApiTest(String path, Class<?>... resourceClasses) {
        super(ApiTest.builder(resourceClasses)
//            .decorateController(b -> b.defaultMediaType(MediaType.APPLICATION_JSON_API))
            .decorateApi(b -> b.jsonapi()), path, 1);
    }


}
