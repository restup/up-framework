package com.github.restup.test.spring;

import com.github.restup.test.RestApiTest.Builder;
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
    private MockMvc mockMvc;
    private boolean jsonapi;

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
        Builder b = new Builder(executor, getClass(), path, pathArgs);
        if (jsonapi) {
            b.jsonapi();
        }
        return b;
    }

    /**
     * {@link #api} will call jsonapi() for testing jsonapi request/responses
     */
    public void jsonapi() {
        this.jsonapi = true;
    }

}
