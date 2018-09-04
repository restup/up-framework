package com.github.restup.controller.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.restup.controller.ResourceController;
import com.github.restup.controller.ResourceController.Builder;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.repository.collections.MapBackedRepositoryFactory;
import com.github.restup.test.RestApiAssertions;
import com.github.restup.test.repository.RepositoryUnit;
import org.junit.Before;

public abstract class AbstractMockTest {

    private final String path;
    private final Object[] pathArgs;
    protected RestApiAssertions.Builder api;
    protected ResourceRegistry registry;

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
        registry = registry(resourceClasses);
        this.path = path;
        this.pathArgs = pathArgs;
    }

    protected AbstractMockTest(String path, Class<?>... resourceClasses) {
    		this(resourceClasses, path, 1);
    }


    public static ResourceRegistry registry(Class<?>... resourceClasses) {
        // build registry setting, minimally passing in a repository factory
        ResourceRegistry registry = ResourceRegistry.builder()
                .repositoryFactory(new MapBackedRepositoryFactory())
        .build();

        registry.registerResources(resourceClasses);
        return registry;
    }

    @Before
    public void before() {
        api = builder(path, pathArgs);
    }

    protected RestApiAssertions.Builder builder(String path, Object... pathArgs) {
        ObjectMapper mapper = new ObjectMapper();
        if (registry == null) {
            registry = registry();
        }
        ResourceController controller = resourceController(registry, mapper);
        MockContentNegotiation contentNegotiation = new MockJacksonContentNegotiation(mapper);
        MockApiExecutor executor = new MockApiExecutor(registry, controller, contentNegotiation);
        RestApiAssertions.Builder b = RestApiAssertions.builder(executor, getRelativeToClass(), path, pathArgs);
        return configureRestApiAssertions(b);
    }

    protected Class<?> getRelativeToClass() {
    		return getClass();
    }
    
    

    public ResourceController resourceController(ResourceRegistry registry, ObjectMapper mapper) {
        // create new resource controller
        // a Spring MVC Controller is configured by UpSpringMVCConfiguration imported above
        return configureResourceController(ResourceController.builder()
                .jacksonObjectMapper(mapper)
            .registry(registry))
                .build();
    }

    protected Builder configureResourceController(ResourceController.Builder b) {
        return b;
    }

    protected RestApiAssertions.Builder configureRestApiAssertions(RestApiAssertions.Builder b) {
        return b;
    }

    protected RepositoryUnit.Loader loader() {
        return loader(getRelativeToClass());
    }

    protected RepositoryUnit.Loader loader(Class<?> relativeTo) {
        return RepositoryUnit.loader().registry(registry).relativeTo(relativeTo);
    }

}
