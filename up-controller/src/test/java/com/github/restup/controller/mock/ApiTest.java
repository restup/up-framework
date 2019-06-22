package com.github.restup.controller.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.restup.controller.ResourceController;
import com.github.restup.controller.ResourceControllerBuilderDecorator;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.repository.collections.MapBackedRepositoryFactory;
import com.github.restup.test.ApiExecutor;
import com.github.restup.test.RestApiAssertions;
import com.github.restup.test.RpcApiAssertionsBuilderDecorator;
import com.github.restup.test.repository.RepositoryUnit;
import com.github.restup.test.resource.RelativeTestResource;
import java.util.ArrayList;
import java.util.List;

public class ApiTest {

    private final ResourceRegistry registry;
    private final ApiExecutor executor;
    private final List<RpcApiAssertionsBuilderDecorator> rpcApiAssertionsBuilderDecorators;
    private final Class<?> relativeToClass;

    public ApiTest(ResourceRegistry registry,
        MockApiExecutor executor,
        List<RpcApiAssertionsBuilderDecorator> rpcApiAssertionsBuilderDecorators,
        Class<?> relativeToClass) {
        this.registry = registry;
        this.executor = executor;
        this.rpcApiAssertionsBuilderDecorators = rpcApiAssertionsBuilderDecorators;
        this.relativeToClass = relativeToClass;
    }

    public static ResourceRegistry registry(Class<?>... resourceClasses) {
        // build registry setting, minimally passing in a repository factory
        ResourceRegistry registry = ResourceRegistry.builder()
            .repositoryFactory(new MapBackedRepositoryFactory())
            .build();
        registry.registerResources(resourceClasses);
        return registry;
    }

    public static Builder builder(Class<?>... resourceClasses) {
        return new Builder().registerResources(resourceClasses);
    }

    public RepositoryUnit.Loader loader() {
        return loader(getRelativeToClass());
    }

    public RepositoryUnit.Loader loader(Class<?> relativeTo) {
        return RepositoryUnit.loader().registry(registry).relativeTo(relativeTo);
    }

    public Class<?> getRelativeToClass() {
        return relativeToClass;
    }

    public RestApiAssertions.Builder getApi(String path, Object... pathArgs) {
        return RestApiAssertions
            .builder(executor, getRelativeToClass(), path, pathArgs)
            .decorate(rpcApiAssertionsBuilderDecorators);
    }

    public ResourceRegistry getRegistry() {
        return registry;
    }

    public static final class Builder {

        private Class<?> relativeTo;
        private Class<?>[] resourceClasses;
        private ResourceRegistry registry;
        private List<ResourceControllerBuilderDecorator> resourceControllerBuilderDecorators = new ArrayList<>();
        private List<RpcApiAssertionsBuilderDecorator> rpcApiAssertionsBuilderDecorators = new ArrayList<>();

        Builder() {
        }

        Builder me() {
            return this;
        }

        public Builder registry(ResourceRegistry registry) {
            this.registry = registry;
            return me();
        }

        public Builder registerResources(Class<?>... resourceClasses) {
            this.resourceClasses = resourceClasses;
            return me();
        }

        public Builder relativeTo(Class<?> relativeTo) {
            this.relativeTo = relativeTo;
            return me();
        }

        public Builder relativeToDefault(Class<?> relativeTo) {
            if (this.relativeTo == null) {
                this.relativeTo = relativeTo;
            }
            return me();
        }

        public Builder decorateController(ResourceControllerBuilderDecorator decorator) {
            resourceControllerBuilderDecorators.add(decorator);
            return me();
        }

        public Builder decorateApi(RpcApiAssertionsBuilderDecorator decorator) {
            rpcApiAssertionsBuilderDecorators.add(decorator);
            return me();
        }

        public ApiTest build() {

            ObjectMapper mapper = new ObjectMapper();
            if (registry == null) {
                registry = ApiTest.registry(resourceClasses);
            } else if (resourceClasses != null) {
                registry.registerResources(resourceClasses);
            }
            ResourceController controller = ResourceController.builder()
                .jacksonObjectMapper(mapper)
                .registry(registry)
                .decorate(resourceControllerBuilderDecorators)
                .build();
            MockContentNegotiation contentNegotiation = new MockJacksonContentNegotiation(mapper);
            MockApiExecutor executor = new MockApiExecutor(registry, controller,
                contentNegotiation);

            if (relativeTo == null) {
                relativeTo = RelativeTestResource.getClassFromStack(ApiExecutor.class, getClass());
            }

            return new ApiTest(registry, executor, rpcApiAssertionsBuilderDecorators,
                relativeTo);
        }
    }
}
