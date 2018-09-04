package com.github.restup.controller.mock;

import com.github.restup.controller.ResourceController.Builder;
import com.github.restup.controller.model.MediaType;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.test.RestApiAssertions;

public class AbstractMockJsonApiTest extends AbstractMockTest {

    protected AbstractMockJsonApiTest(String path, Object... pathArgs) {
        super(path, pathArgs);
    }

    protected AbstractMockJsonApiTest(ResourceRegistry registry,
        String path, Object... pathArgs) {
        super(registry, path, pathArgs);
    }

    protected AbstractMockJsonApiTest(Class<?>[] resourceClasses, String path, Object... pathArgs) {
        super(resourceClasses, path, pathArgs);
    }

    protected AbstractMockJsonApiTest(String path, Class<?>... resourceClasses) {
        super(path, resourceClasses);
    }


    @Override
    protected Builder configureResourceController(Builder b) {
        return b.defaultMediaType(MediaType.APPLICATION_JSON_API);
    }

    @Override
    protected RestApiAssertions.Builder configureRestApiAssertions(RestApiAssertions.Builder b) {
        return b.jsonapi();
    }

}
