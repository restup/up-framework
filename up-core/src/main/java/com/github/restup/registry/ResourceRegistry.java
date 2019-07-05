package com.github.restup.registry;

import com.github.restup.annotations.model.CreateStrategy;
import com.github.restup.annotations.model.DeleteStrategy;
import com.github.restup.annotations.model.UpdateStrategy;
import com.github.restup.bind.MethodArgumentFactory;
import com.github.restup.bind.converter.ConverterFactory;
import com.github.restup.config.ConfigurationContext;
import com.github.restup.config.UpFactories;
import com.github.restup.errors.ErrorFactory;
import com.github.restup.mapping.MappedClass;
import com.github.restup.mapping.MappedClassBuilderDecorator;
import com.github.restup.mapping.MappedClassFactory;
import com.github.restup.mapping.MappedClassRegistry;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.mapping.fields.MappedFieldBuilderDecorator;
import com.github.restup.mapping.fields.MappedFieldFactory;
import com.github.restup.path.ResourcePathsProvider;
import com.github.restup.query.Pagination;
import com.github.restup.registry.settings.ControllerMethodAccess;
import com.github.restup.registry.settings.RegistrySettings;
import com.github.restup.registry.settings.ServiceMethodAccess;
import com.github.restup.repository.RepositoryFactory;
import com.github.restup.response.strategy.CreateStrategySupplier;
import com.github.restup.response.strategy.DeleteStrategySupplier;
import com.github.restup.response.strategy.UpdateStrategySupplier;
import com.github.restup.service.model.request.RequestObjectFactory;
import com.github.restup.util.Streams;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import javax.validation.Validator;

/**
 * A registry of application {@link Resource}s, containing a {@link Resource}, containing meta data,
 * field mappings, repository, and service details for each registered each resource
 * <p>
 * A singleton instance exists for convenience, but it is possible to construct multiple
 * {@link ResourceRegistry}s instances if needed.
 *
 * @author andy.buttaro
 */
public interface ResourceRegistry extends MappedClassRegistry {

    static Builder builder() {
        return new Builder();
    }

    void registerResource(Resource.Builder<?, ?> b);

    default void registerResource(Class<?> resourceClass) {
        registerResource(Resource.builder(resourceClass));
    }

    default void registerResources(Class<?>... resourceClasses) {
        Streams.forEach(resourceClasses, this::registerResource);
    }

    default void registerResources(Collection<Class<?>> collection) {
        collection.stream().forEach(c -> registerResource(c));
    }

    RegistrySettings getSettings();

    Resource<?, ?> getResource(String resourceName);

    Resource<?, ?> getResourceByPluralName(String pluralName);

    <T> Resource<T, ?> getResource(Class<T> resourceClass);

    Collection<Resource<?, ?>> getResources();

    void registerResource(Resource<?, ?> resource);

    boolean hasResource(String resourceName);

    boolean hasResource(Class<?> resourceClass);

    boolean hasMappedClass(Class<?> mappedClass);

    void registerMappedClass(MappedClass<?> mappedClass);

    ResourceRelationship<?, ?, ?, ?> getRelationship(String from, String to);

    ResourceRelationship<?, ?, ?, ?> getRelationship(Resource<?, ?> from, Resource<?, ?> to);

    Collection<ResourceRelationship<?, ?, ?, ?>> getRelationships(String resourceName);

    final class Builder {
        private final RegistrySettings.Builder settings;
        private ConfigurationContext configurationContext;
        private List<ResourceRegistryBuilderDecorator> resourceRegistryBuilderDecorators = new ArrayList<>();

        public Builder() {
            settings = RegistrySettings.builder();
        }

        private Builder me() {
            return this;
        }

        public Builder resourceRegistryRepository(ResourceRegistryRepository resourceRegistryMap) {
            settings.resourceRegistryRepository(resourceRegistryMap);
            return me();
        }

        public Builder configurationContext(ConfigurationContext configurationContext) {
            this.configurationContext = configurationContext;
            return me();
        }

        public Builder packagesToScan(String... packagesToScan) {
            settings.packagesToScan(packagesToScan);
            return me();
        }

        public Builder mappedFieldOrderComparator(Comparator<MappedField<?>> mappedFieldOrderComparator) {
            settings.mappedFieldOrderComparator(mappedFieldOrderComparator);
            return me();
        }

        public Builder converterFactory(ConverterFactory converterFactory) {
            settings.converterFactory(converterFactory);
            return me();
        }

        public Builder mappedFieldFactory(MappedFieldFactory mappedFieldFactory) {
            settings.mappedFieldFactory(mappedFieldFactory);
            return me();
        }

        public Builder mappedFieldBuilderDecoratorBuilder(
            MappedFieldBuilderDecorator.Builder builder) {
            settings.mappedFieldBuilderDecoratorBuilder(builder);
            return me();
        }

        public Builder mappedClassBuilderDecoratorBuilder(
            MappedClassBuilderDecorator.Builder builder) {
            settings.mappedClassBuilderDecoratorBuilder(builder);
            return me();
        }

        public Builder repositoryFactory(RepositoryFactory repositoryFactory) {
            settings.repositoryFactory(repositoryFactory);
            return me();
        }

        public Builder errorFactory(ErrorFactory errorFactory) {
            settings.errorFactory(errorFactory);
            return me();
        }

        public Builder methodArgumentFactory(MethodArgumentFactory methodArgumentFactory) {
            settings.methodArgumentFactory(methodArgumentFactory);
            return me();
        }

        public Builder serviceMethodAccess(ServiceMethodAccess defaultServiceMethodAccess) {
            settings.serviceMethodAccess(defaultServiceMethodAccess);
            return me();
        }

        public Builder controllerMethodAccess(ControllerMethodAccess defaultControllerMethodAccess) {
            settings.controllerMethodAccess(defaultControllerMethodAccess);
            return me();
        }

        public Builder requestObjectFactory(RequestObjectFactory requestObjectFactory) {
            settings.requestObjectFactory(requestObjectFactory);
            return me();
        }

        public Builder mappedClassFactory(MappedClassFactory mappedClassFactory) {
            settings.mappedClassFactory(mappedClassFactory);
            return me();
        }

        public Builder excludeFrameworkFilters(boolean excludeFrameworkFilters) {
            settings.excludeFrameworkFilters(excludeFrameworkFilters);
            return me();
        }

        public Builder defaultServiceFilters(Object... filters) {
            settings.defaultServiceFilters(filters);
            return me();
        }

        public Builder defaultPagination(Pagination defaultPagination) {
            settings.defaultPagination(defaultPagination);
            return me();
        }

        public Builder defaultPaginationDisabled() {
            settings.defaultPaginationDisabled();
            return me();
        }

        public Builder defaultPagination(Integer pageLimit, Integer pageOffset,
                boolean withTotalsDisabled) {
            settings.defaultPagination(pageLimit, pageOffset, withTotalsDisabled);
            return me();
        }

        public Builder defaultPagination(Integer pageLimit) {
            settings.defaultPagination(pageLimit);
            return me();
        }

        public Builder validator(Validator validator) {
            settings.validator(validator);
            return me();
        }

        public Builder defaultRestrictedFieldsProvider(ResourcePathsProvider restrictedFieldsProvider) {
            settings.defaultRestrictedFieldsProvider(restrictedFieldsProvider);
            return me();
        }

        public Builder defaultSparseFieldsProvider(ResourcePathsProvider defaultSparseFieldsProvider) {
            settings.defaultSparseFieldsProvider(defaultSparseFieldsProvider);
            return me();
        }

        public Builder createStrategySupplier(CreateStrategySupplier strategy) {
            settings.createStrategySupplier(strategy);
            return me();
        }

        public Builder createStrategy(CreateStrategy strategy) {
            settings.createStrategy(strategy);
            return me();
        }

        public Builder updateStrategySupplier(UpdateStrategySupplier strategy) {
            settings.updateStrategySupplier(strategy);
            return me();
        }

        public Builder updateStrategy(UpdateStrategy strategy) {
            settings.updateStrategy(strategy);
            return me();
        }

        public Builder deleteStrategySupplier(DeleteStrategySupplier strategy) {
            settings.deleteStrategySupplier(strategy);
            return me();
        }

        public Builder deleteStrategy(DeleteStrategy strategy) {
            settings.deleteStrategy(strategy);
            return me();
        }

        public Builder basePath(String basePath) {
            settings.basePath(basePath);
            return me();
        }

        public Builder decorate(ResourceRegistryBuilderDecorator... decorators) {
            for (ResourceRegistryBuilderDecorator decorator : decorators) {
                resourceRegistryBuilderDecorators.add(decorator);
            }
            return me();
        }

        public Builder decorate(Collection<ResourceRegistryBuilderDecorator> decorators) {
            resourceRegistryBuilderDecorators.addAll(decorators);
            return me();
        }

        public ResourceRegistry build() {
            if (configurationContext == null) {
                configurationContext = ConfigurationContext.getDefault();
            }
            settings.configurationContext(configurationContext);
            apply(UpFactories.getInstance()
                .getInstances(configurationContext, ResourceRegistryBuilderDecorator.class));
            apply(resourceRegistryBuilderDecorators);
            return new DefaultResourceRegistry(settings.build());
        }

        private void apply(
            List<ResourceRegistryBuilderDecorator> decorators) {
            decorators.stream().forEach(d -> d.decorate(configurationContext, this));
        }

    }

}
