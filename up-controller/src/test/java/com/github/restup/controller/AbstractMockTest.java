package com.github.restup.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.restup.controller.mock.MockApiExecutor;
import com.github.restup.controller.mock.MockContentNegotiation;
import com.github.restup.controller.mock.MockJacksonContentNegotiation;
import com.github.restup.controller.model.MediaType;
import com.github.restup.test.repository.RepositoryUnit;
import com.university.Course;
import com.university.Student;
import com.university.University;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.registry.settings.RegistrySettings;
import com.github.restup.repository.collections.MapBackedRepositoryFactory;
import com.github.restup.test.RestApiTest;
import org.junit.Before;

public abstract class AbstractMockTest {

    private final String path;
    private final Object[] pathArgs;
    protected RestApiTest.Builder api;
    private boolean jsonapi;

    protected ResourceRegistry registry;

    protected AbstractMockTest(String path, Object... pathArgs) {
        this.path = path;
        this.pathArgs = pathArgs;
    }

    @Before
    public void before() {
        api = builder(path, pathArgs);
    }

    protected RestApiTest.Builder builder(String path, Object... pathArgs) {
        ObjectMapper mapper = new ObjectMapper();
        if ( registry == null ) {
            registry = registry();
        }
        ResourceController controller = resourceController(registry, mapper);
        MockContentNegotiation contentNegotiation = new MockJacksonContentNegotiation(mapper);
        MockApiExecutor executor = new MockApiExecutor(registry, controller, contentNegotiation);
        RestApiTest.Builder b = new RestApiTest.Builder(executor, getClass(), path, pathArgs);
        if (jsonapi) {
            b.jsonapi();
        }
        return b;
    }

    public ResourceRegistry registry() {
        // build registry setting, minimally passing in a repository factory
        ResourceRegistry registry = new ResourceRegistry(RegistrySettings.builder()
                .repositoryFactory(new MapBackedRepositoryFactory())
        );

        registry.registerResource(Course.class
                , Student.class
                , University.class);
        return registry;
    }

    public ResourceController resourceController(ResourceRegistry registry, ObjectMapper mapper) {
        // create new resource controller
        // a Spring MVC Controller is configured by UpSpringMVCConfiguration imported above
        return ResourceController.builder()
                .jacksonObjectMapper(mapper)
                .registry(registry)
                .defaultMediaType(MediaType.APPLICATION_JSON_API)
                .build();
    }

    /**
     * {@link #api} will call jsonapi() for testing jsonapi request/responses
     */
    public void jsonapi() {
        this.jsonapi = true;
    }

    protected RepositoryUnit.Loader loader() {
        return RepositoryUnit.loader().registry(registry);
    }

}
