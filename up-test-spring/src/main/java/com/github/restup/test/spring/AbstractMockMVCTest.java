package com.github.restup.test.spring;

import com.github.restup.registry.ResourceRegistry;
import com.github.restup.test.RestApiAssertions;
import com.github.restup.test.RestApiAssertions.Builder;
import com.github.restup.test.repository.RepositoryUnit;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Abstract implementation for convenience, autowiring mockMvc and setting up a {@link Builder}
 */
@RunWith(SpringRunner.class)
public abstract class AbstractMockMVCTest {

    private final String path;
    private final Object[] pathArgs;
    protected Builder api;
    @Autowired
    MockMvc mockMvc;
    private boolean jsonapi;
    @Autowired
    private ResourceRegistry registry;

    protected AbstractMockMVCTest(String path, Object... pathArgs) {
        this.path = path;
        this.pathArgs = pathArgs;
    }

    @Before
    public void before() {
        api = builder(path, pathArgs);
    }

    protected Builder builder(String path, Object... pathArgs) {
        MockMVCApiExecutor executor = new MockMVCApiExecutor(mockMvc);
        Builder b = RestApiAssertions.builder(executor, getClass(), path, pathArgs);
        if (jsonapi) {
            b.jsonapi();
        }
        return b;
    }

    /**
     * {@link #api} will call jsonapi() for testing jsonapi request/responses
     */
    public void jsonapi() {
        jsonapi = true;
    }


    protected RepositoryUnit.Loader loader() {
        return loader(getRelativeToClass());
    }

    protected RepositoryUnit.Loader loader(Class<?> relativeTo) {
        return RepositoryUnit.loader().registry(registry).relativeTo(relativeTo);
    }

    protected Class<?> getRelativeToClass() {
        return getClass();
    }

}
