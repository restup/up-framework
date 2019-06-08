package com.github.restup.registry.settings;

import com.github.restup.bind.MethodArgumentFactory;
import com.github.restup.bind.converter.ConverterFactory;
import com.github.restup.bind.converter.ParameterConverterFactory;
import com.github.restup.errors.ErrorFactory;
import com.github.restup.mapping.MappedClassFactory;
import com.github.restup.mapping.MappedClassRegistry;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.mapping.fields.MappedFieldFactory;
import com.github.restup.path.ResourcePathsProvider;
import com.github.restup.query.Pagination;
import com.github.restup.registry.ResourceRegistryRepository;
import com.github.restup.repository.RepositoryFactory;
import com.github.restup.service.model.request.RequestObjectFactory;
import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.List;

/**
 * Configures settings and implementations to be used by registry.
 */
public class BasicRegistrySettings implements RegistrySettings {

    private final ResourceRegistryRepository resourceRegistryRepository;
    private final MappedClassFactory mappedClassFactory;
    private final MappedClassRegistry mappedClassRegistry;
    private final List<String> packagesToScan;
    private final MappedFieldFactory mappedFieldFactory;
    private final Comparator<MappedField<?>> mappedFieldOrderComparator;

    private final ControllerMethodAccess defaultControllerAccess;
    private final ServiceMethodAccess defaultServiceAccess;
    private final Pagination defaultPagination;
    private final ResourcePathsProvider defaultSparseFieldsProvider;
    private final ResourcePathsProvider defaultRestrictedFieldsProvider;

    private final RepositoryFactory repositoryFactory;
    private final ErrorFactory errorFactory;
    private final RequestObjectFactory requestObjectFactory;
    private final MethodArgumentFactory methodArgumentFactory;
    private final ConverterFactory converterFactory;
    private final ParameterConverterFactory parameterConverterFactory;
    private final List<Object> defaultServiceFilters;
    private final String basePath;

    BasicRegistrySettings(ResourceRegistryRepository resourceRegistryMap,
        MappedClassFactory mappedClassFactory,
        String[] packagesToScan, MappedFieldFactory mappedFieldFactory,
        Comparator<MappedField<?>> mappedFieldOrderComparator,
        ControllerMethodAccess defaultControllerMethodAccess,
        ServiceMethodAccess defaultServiceMethodAccess,
        RepositoryFactory repositoryFactory, ErrorFactory errorFactory,
        RequestObjectFactory requestObjectFactory,
        MethodArgumentFactory methodArgumentFactory, ConverterFactory converterFactory,
        ParameterConverterFactory parameterConverterFactory, Object[] defaultServiceFilters,
        Pagination defaultPagination, ResourcePathsProvider defaultSparseFieldsProvider,
        ResourcePathsProvider defaultRestrictedFieldsProvider, String basePath) {
        this.packagesToScan = ImmutableList.copyOf(packagesToScan);
        this.mappedFieldFactory = mappedFieldFactory;
        this.mappedFieldOrderComparator = mappedFieldOrderComparator;
        defaultControllerAccess = defaultControllerMethodAccess;
        defaultServiceAccess = defaultServiceMethodAccess;
        this.repositoryFactory = repositoryFactory;
        this.errorFactory = errorFactory;
        this.requestObjectFactory = requestObjectFactory;
        this.converterFactory = converterFactory;
        this.parameterConverterFactory = parameterConverterFactory;
        this.defaultServiceFilters = ImmutableList.copyOf(defaultServiceFilters);
        this.defaultPagination = defaultPagination;
        this.defaultSparseFieldsProvider = defaultSparseFieldsProvider;
        this.defaultRestrictedFieldsProvider = defaultRestrictedFieldsProvider;
        this.basePath = basePath;

        // wrap with operations
        RegistryOperations operations = new RegistryOperations(resourceRegistryMap,
            mappedClassFactory);
        this.mappedClassFactory = mappedClassFactory;
        mappedClassRegistry = operations;
        resourceRegistryRepository = operations;

        if (methodArgumentFactory == null) {
            this.methodArgumentFactory = MethodArgumentFactory
                .getDefaultInstance(mappedClassRegistry, this.parameterConverterFactory);
        } else {
            this.methodArgumentFactory = methodArgumentFactory;
        }
    }

    @Override
    public ResourceRegistryRepository getResourceRegistryRepository() {
        return resourceRegistryRepository;
    }

    @Override
    public MappedClassRegistry getMappedClassRegistry() {
        return mappedClassRegistry;
    }

    @Override
    public MappedClassFactory getMappedClassFactory() {
        return mappedClassFactory;
    }

    @Override
    public List<String> getPackagesToScan() {
        return packagesToScan;
    }

    @Override
    public MappedFieldFactory getMappedFieldFactory() {
        return mappedFieldFactory;
    }

    @Override
    public Comparator<MappedField<?>> getMappedFieldOrderComparator() {
        return mappedFieldOrderComparator;
    }

    @Override
    public RepositoryFactory getRepositoryFactory() {
        return repositoryFactory;
    }


    @Override
    public ErrorFactory getErrorFactory() {
        return errorFactory;
    }

    @Override
    public MethodArgumentFactory getMethodArgumentFactory() {
        return methodArgumentFactory;
    }

    @Override
    public ConverterFactory getConverterFactory() {
        return converterFactory;
    }

    @Override
    public ParameterConverterFactory getParameterConverterFactory() {
        return parameterConverterFactory;
    }

    @Override
    public ControllerMethodAccess getDefaultControllerAccess() {
        return defaultControllerAccess;
    }

    @Override
    public ServiceMethodAccess getDefaultServiceAccess() {
        return defaultServiceAccess;
    }

    @Override
    public List<Object> getDefaultServiceFilters() {
        return defaultServiceFilters;
    }

    @Override
    public RequestObjectFactory getRequestObjectFactory() {
        return requestObjectFactory;
    }

    @Override
    public Pagination getDefaultPagination() {
        return defaultPagination;
    }

    @Override
    public ResourcePathsProvider getDefaultRestrictedFieldsProvider() {
        return defaultRestrictedFieldsProvider;
    }

    @Override
    public ResourcePathsProvider getDefaultSparseFieldsProvider() {
        return defaultSparseFieldsProvider;
    }

    @Override
    public String getBasePath() {
        return basePath;
    }

}
