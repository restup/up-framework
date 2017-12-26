package com.github.restup.controller;

import org.junit.Before;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.restup.controller.mock.MockApiExecutor;
import com.github.restup.controller.mock.MockContentNegotiation;
import com.github.restup.controller.mock.MockJacksonContentNegotiation;
import com.github.restup.controller.model.MediaType;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.registry.settings.RegistrySettings;
import com.github.restup.repository.collections.MapBackedRepositoryFactory;
import com.github.restup.test.RestApiTest;
import com.github.restup.test.repository.RepositoryUnit;

public abstract class AbstractMockTest {

    private final String path;
    private final Object[] pathArgs;
    protected RestApiTest.Builder api;
    protected ResourceRegistry registry;
    private boolean jsonapi;

    protected AbstractMockTest(String path, Object... pathArgs) {
        this.path = path;
        this.pathArgs = pathArgs;
    }

    protected AbstractMockTest(ResourceRegistry registry, String path, Object... pathArgs) {
    		this.registry = registry;
        this.path = path;
        this.pathArgs = pathArgs;
    }

    protected AbstractMockTest(Class<?>[] resourceClasses, String path, Object... pathArgs) {
    		this.registry = registry(resourceClasses);
        this.path = path;
        this.pathArgs = pathArgs;
    }

    protected AbstractMockTest(String path, Class<?>... resourceClasses) {
    		this(resourceClasses, path, 1);
    }


    public static ResourceRegistry registry(Class<?>... resourceClasses) {
        // build registry setting, minimally passing in a repository factory
        ResourceRegistry registry = new ResourceRegistry(RegistrySettings.builder()
                .repositoryFactory(new MapBackedRepositoryFactory())
        );

        registry.registerResource(resourceClasses);
        return registry;
    }

    @Before
    public void before() {
        api = builder(path, pathArgs);
    }

    protected RestApiTest.Builder builder(String path, Object... pathArgs) {
        ObjectMapper mapper = new ObjectMapper();
        if (registry == null) {
            registry = registry();
        }
        ResourceController controller = resourceController(registry, mapper);
        MockContentNegotiation contentNegotiation = new MockJacksonContentNegotiation(mapper);
        MockApiExecutor executor = new MockApiExecutor(registry, controller, contentNegotiation);
        RestApiTest.Builder b = new RestApiTest.Builder(executor, getRelativeToClass(), path, pathArgs);
        if (jsonapi) {
            b.jsonapi();
        }
        return b;
    }
    
    protected Class<?> getRelativeToClass() {
    		return getClass();
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
        return loader(getRelativeToClass());
    }

    protected RepositoryUnit.Loader loader(Class<?> relativeTo) {
        return RepositoryUnit.loader().registry(registry).relativeTo(relativeTo);
    }

}
