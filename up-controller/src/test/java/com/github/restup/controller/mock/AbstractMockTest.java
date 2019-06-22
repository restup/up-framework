package com.github.restup.controller.mock;

import com.github.restup.registry.ResourceRegistry;
import com.github.restup.test.RestApiAssertions;
import com.github.restup.test.repository.RepositoryUnit;

public abstract class AbstractMockTest {

    protected final RestApiAssertions.Builder api;
    private final ApiTest apiTest;
    protected ResourceRegistry registry;

    protected AbstractMockTest(String path, Object... pathArgs) {
        this(ApiTest.builder(), path, pathArgs);
    }

    protected AbstractMockTest(ApiTest.Builder builder, String path, Object... pathArgs) {
        apiTest = builder.relativeToDefault(getClass()).build();
        api = apiTest.getApi(path, pathArgs);
    }

    protected AbstractMockTest(Class<?>[] resourceClasses, String path, Object... pathArgs) {
        this(ApiTest.builder(resourceClasses), path, pathArgs);
    }

    protected AbstractMockTest(String path, Class<?>... resourceClasses) {
    		this(resourceClasses, path, 1);
    }

    protected RestApiAssertions.Builder getApi(String path, Object... pathArgs) {
        return apiTest.getApi(path, pathArgs);
    }

    protected RepositoryUnit.Loader loader() {
        return apiTest.loader();
    }

}
