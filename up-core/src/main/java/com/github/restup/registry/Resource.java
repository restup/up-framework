package com.github.restup.registry;

import static com.github.restup.util.UpUtils.nvl;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import com.github.restup.annotations.model.CreateStrategy;
import com.github.restup.annotations.model.DeleteStrategy;
import com.github.restup.annotations.model.UpdateStrategy;
import com.github.restup.mapping.MappedClass;
import com.github.restup.mapping.MappedClassRegistry;
import com.github.restup.mapping.UntypedClass;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.path.ResourcePath;
import com.github.restup.path.ResourcePathsProvider;
import com.github.restup.query.Pagination;
import com.github.restup.registry.settings.ControllerMethodAccess;
import com.github.restup.registry.settings.RegistrySettings;
import com.github.restup.registry.settings.ServiceMethodAccess;
import com.github.restup.repository.AnnotatedResourceRepository;
import com.github.restup.repository.RepositoryFactory;
import com.github.restup.repository.ResourceRepositoryOperations;
import com.github.restup.service.AnnotatedService;
import com.github.restup.service.DelegatingResourceService;
import com.github.restup.service.FilteredService;
import com.github.restup.service.ResourceService;
import com.github.restup.service.ResourceServiceOperations;
import com.github.restup.util.Assert;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Resource meta data, defining resource name, type, service implementation, etc.
 */
public interface Resource<T, ID extends Serializable> extends Comparable<Resource<T, ID>> {

    /**
     * Ensures basepath always has leading and trailing / so it is prepared for url concatenation
     *
     * @param basePath to clean
     * @return clean base path
     */
    static String cleanBasePath(String basePath) {
        String result = basePath;
        if (result != null) {
            if (!result.startsWith("/")) {
                result = "/" + result;
            }
            if (!result.endsWith("/")) {
                result = result + "/";
            }
        }
        return result;
    }

    static List<ResourcePath> getAllPaths(Resource<?, ?> resource) {
        return resource == null ? Collections.emptyList() : resource.getAllPaths();
    }

    static <T, ID extends Serializable> Set<ID> getIds(Resource<T, ID> resource, List<T> list) {
        Set<ID> result;
        int size = CollectionUtils.size(list);
        if (size < 1) {
            result = Collections.emptySet();
        } else {
            result = list.stream()
                .map(t -> resource.getIdentityField().readValue(t))
                .collect(Collectors.toSet());
        }
        return result;
    }

    static List<ResourcePath> getPaths(Resource<?, ?> resource, boolean includeTransient,
        boolean apiFieldsOnly) {
        // TODO better to cache immutable paths?
        return resource.getMapping().getAttributes()
            .stream()
            .filter(mf -> includeTransient || !mf.isTransientField())
            .filter(mf -> !apiFieldsOnly || mf.isApiProperty())
            .map(mf -> ResourcePath.path(resource, mf))
            .collect(Collectors.toList());
    }

    static <T> T validate(Resource<T, ?> resource, T o) {
        MappedClass<T> mapping = resource.getMapping();
        // if an object is
        if (mapping.isTypedMapPresent()) {
            if (o instanceof Iterable) {
                Iterator<T> it = ((Iterable<T>) o).iterator();
                while (it.hasNext()) {
                    validate(resource, it.next());
                }
            } else {
                mapping.getAttributes()
                    .forEach(mappedField -> {
                        MappedField<Object> mf = (MappedField<Object>) mappedField;
                        if (o instanceof Map) {
                            Object value = mappedField.readValue(o);
                            if (value != null) {
                                Object converted = resource.getRegistry().getSettings()
                                    .getConverterFactory().convert(value, mf.getType());
                                mf.writeValue(o, converted);
                            }
                        }
                    });
            }
        }
        return o;
    }

    static <T, ID extends Serializable> Builder<T, ID> builder(Class<T> resourceClass) {
        return new Builder<>(resourceClass);
    }

    static <T, ID extends Serializable> Builder<T, ID> builder(Class<T> resourceClass,
        Class<ID> idClass) {
        return new Builder<>(resourceClass);
    }

    static <T, ID extends Serializable> Builder<T, ID> builder() {
        return new Builder<>();
    }

    /**
     * @return A List of all paths for the resource
     */
    List<ResourcePath> getAllPaths();

    /**
     * @return The Type (Class or {@link UntypedClass}) of a resource
     */
    Type getType();

    default Class<T> getClassType() {
        Type type = getType();
        if (type instanceof Class) {
            return (Class<T>) type;
        } else if (type instanceof UntypedClass) {
            return ((UntypedClass<T>) type).getContainer();
        }
        throw new IllegalStateException("type must be Class or UntypedClass");
    }

    /**
     * @return resource name
     */
    String getName();

    /**
     * @return pluralized resource name
     */
    String getPluralName();

    /**
     * @return Mapping for the for resource
     */
    MappedClass<T> getMapping();

    /**
     * @return identity maping for the resource
     */
    MappedField<ID> getIdentityField();

    /**
     * @return service implementation
     */
    ResourceService<T, ID> getService();

    ResourceServiceOperations getServiceOperations();

    ResourceRepositoryOperations getRepositoryOperations();

    ControllerMethodAccess getControllerMethodAccess();

    ServiceMethodAccess getServiceMethodAccess();

    ResourceRegistry getRegistry();

    Pagination getDefaultPagination();

    /**
     * @return the fields returned by default for sparse fields requests.
     */
    List<ResourcePath> getDefaultSparseFields();

    /**
     * @return any fields to which the requestor is not permitted to read
     */
    List<ResourcePath> getRestrictedFields();

    String getBasePath();

    Collection<ResourceRelationship<?, ?, ?, ?>> getRelationships();

    default List<ResourceRelationship<?, ?, ?, ?>> getRelationshipsTo() {
        Collection<ResourceRelationship<?, ?, ?, ?>> relationships = getRelationships();
        if (relationships != null) {
            return relationships.stream()
                .filter(relationship -> relationship.isTo(this))
                .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    default MappedField<?> findApiField(String field) {
        return ResourcePath.findApiField(getMapping(), field);
    }

    default MappedField<?> findPersistedField(String field) {
        return ResourcePath.findPersistedField(getMapping(), field);
    }

    default MappedField<?> findBeanField(String field) {
        return ResourcePath.findBeanField(getMapping(), field);
    }

    default boolean hasApiField(String field) {
        return findApiField(field) != null;
    }

    default boolean hasPersistedField(String field) {
        return findPersistedField(field) != null;
    }

    default boolean hasBeanField(String field) {
        return findBeanField(field) != null;
    }

    default List<ResourcePath> getPaths(boolean includeTransient) {
        return getPaths(this, includeTransient, false);
    }

    @Override
    default int compareTo(Resource<T, ID> o) {
        if (o == null) {
            return -1;
        }
        return getName().compareTo(o.getName());
    }

    CreateStrategy getCreateStrategy();

    UpdateStrategy getUpdateStrategy();

    DeleteStrategy getDeleteStrategy();

    final class Builder<T, ID extends Serializable> {

        private final Type type;
        private ResourceRegistry registry;
        private Object repository;
        private Object service;
        private Object[] serviceFilters;
        private boolean excludeDefaultServiceFilters;
        private ControllerMethodAccess controllerAccess;
        private ServiceMethodAccess serviceAccess;
        private String name;
        private String pluralName;
        private String basePath;
        private Pagination defaultPagination;
        private ResourcePathsProvider sparseFieldsDefaultsProvider;
        private ResourcePathsProvider restrictedFieldsProvider;
        private MappedClass<T> mappedClass;
        private MappedClassRegistry mappedClassRegistry;
        private CreateStrategy createStrategy;
        private UpdateStrategy updateStrategy;
        private DeleteStrategy deleteStrategy;

        Builder(Type resourceClass) {
            Assert.notNull(resourceClass, "resource class must not be null");
            type = resourceClass;
        }

        Builder() {
            this(new UntypedClass<>());
        }

        /**
         * Get pagination from configured value or
         * default to the registry default settings.
         */
        static Pagination getPagination(Pagination configured, Object service,
            Object repository,
            RegistrySettings registrySettings) {
            Pagination pagination = configured;
            if (pagination == null) {
                if (pagination == null) {
                    pagination = registrySettings.getDefaultPagination();
                }
            }
            return pagination;
        }

        Builder<T, ID> me() {
            return this;
        }

        public Builder<T, ID> registry(ResourceRegistry registry) {
            this.registry = registry;
            return me();
        }

        public Builder<T, ID> name(String name) {
            this.name = name;
            return me();
        }

        public Builder<T, ID> pluralName(String pluralName) {
            this.pluralName = pluralName;
            return me();
        }

        public Builder<T, ID> basePath(String basePath) {
            this.basePath = basePath;
            return me();
        }

        public Builder<T, ID> controllerMethodAccess(ControllerMethodAccess controllerAccess) {
            this.controllerAccess = controllerAccess;
            return me();
        }

        public Builder<T, ID> serviceMethodAccess(ServiceMethodAccess serviceAccess) {
            this.serviceAccess = serviceAccess;
            return me();
        }

        public Builder<T, ID> createStrategy(CreateStrategy createStrategy) {
            this.createStrategy = createStrategy;
            return me();
        }

        public Builder<T, ID> updateStrategy(UpdateStrategy updateStrategy) {
            this.updateStrategy = updateStrategy;
            return me();
        }

        public Builder<T, ID> deleteStrategy(DeleteStrategy deleteStrategy) {
            this.deleteStrategy = deleteStrategy;
            return me();
        }

        public Builder<T, ID> repository(Object repository) {
            this.repository = repository;
            return me();
        }

        public Builder<T, ID> service(Object service) {
            this.service = service;
            return me();
        }

        public Builder<T, ID> serviceFilters(Object... serviceFilters) {
            this.serviceFilters = serviceFilters;
            return me();
        }

        public Builder<T, ID> excludeFrameworkFilters(boolean excludeDefaultServiceFilters) {
            this.excludeDefaultServiceFilters = excludeDefaultServiceFilters;
            return me();
        }

        public Builder<T, ID> restrictedFieldsProvider(ResourcePathsProvider restrictedFieldsProvider) {
            this.restrictedFieldsProvider = restrictedFieldsProvider;
            return me();
        }

        public Builder<T, ID> sparseFieldsProvider(ResourcePathsProvider sparseFieldsDefaultsProvider) {
            this.sparseFieldsDefaultsProvider = sparseFieldsDefaultsProvider;
            return me();
        }

        public Builder<T, ID> defaultPagination(Pagination defaultPagination) {
            this.defaultPagination = defaultPagination;
            return me();
        }

        /**
         * {@link #defaultPagination(Integer, Integer, boolean)} with offset of 0, and totals enabled.
         *
         * @param pageLimit value for pagination
         * @return this builder
         */
        public Builder<T, ID> defaultPagination(Integer pageLimit) {
            return defaultPagination(pageLimit, 0, false);
        }

        /**
         * @param pageLimit value for pagination
         * @param pageOffset value for pagination
         * @param withTotalsDisabled to enable/disable totals included in result
         * @return this builder
         */
        public Builder<T, ID> defaultPagination(Integer pageLimit, Integer pageOffset, boolean withTotalsDisabled) {
            return defaultPagination(Pagination.of(pageLimit, pageOffset, withTotalsDisabled));
        }

        public Builder<T, ID> mappedClassFactory(MappedClassRegistry mappedClassRegsitry) {
            mappedClassRegistry = mappedClassRegsitry;
            return me();
        }

        Builder<T, ID> mapping(MappedClass<T> mappedClass) {
            this.mappedClass = mappedClass;
            return me();
        }

        public Builder<T, ID> mapping(MappedClass.Builder<T> builder) {
            builder.defaultName(name);
            return mapping(builder.build());
        }

        public Resource<T, ID> build() {
            Assert.notNull(type, "resource class must not be null");
            Assert.notNull(registry, "registry must not be null");

            RegistrySettings registrySettings = registry.getSettings();

            ControllerMethodAccess controllerMethodAccess = controllerAccess;
            if (controllerMethodAccess == null) {
                controllerMethodAccess = registrySettings.getDefaultControllerAccess();
            }

            MappedClass<?> mapping = mappedClass;
            if (mapping == null) {
                MappedClassRegistry mappedClassFactory = mappedClassRegistry;
                if (mappedClassFactory == null) {
                    mappedClassFactory = registrySettings.getMappedClassRegistry();
                }
                Assert.notNull(mappedClassFactory, "mappedClassFactory must not be null");

                mapping = mappedClassFactory.getMappedClass(type);
            }

            ServiceMethodAccess serviceMethodAccess = serviceAccess;
            if (serviceMethodAccess == null) {
                boolean createDisabled = Objects
                    .equals(mapping.getCreateStrategy(), CreateStrategy.NOT_ALLOWED);
                boolean updateDisabled = Objects
                    .equals(mapping.getUpdateStrategy(), UpdateStrategy.NOT_ALLOWED);
                boolean deleteDisabled = Objects
                    .equals(mapping.getDeleteStrategy(), DeleteStrategy.NOT_ALLOWED);

                if (createDisabled || updateDisabled || deleteDisabled) {
                    serviceMethodAccess = ServiceMethodAccess.builder()
                        .setCreateDisabled(createDisabled)
                        .setPatchDisabled(updateDisabled)
                        .setDeleteDisabled(deleteDisabled)
                        .build();
                } else {
                    serviceMethodAccess = registrySettings.getDefaultServiceAccess();
                }
            }

            Assert.notNull(mapping, "mapping must not be null");
            Assert.notNull(mapping.getAttributes(), "attributes must not be null");

            MappedField<ID> identityField = (MappedField) MappedField.getIdentityField(mapping.getAttributes());

            Assert.notNull(identityField, "identityfield not found.");

            String name = this.name;
            if (isEmpty(name)) {
                name = mapping.getName();
            }
            String pluralName = this.pluralName;
            if (isEmpty(pluralName)) {
                pluralName = mapping.getPluralName();
            }
            String basePath = Resource.cleanBasePath(this.basePath);
            if (basePath == null) {
                basePath = registrySettings.getBasePath();
            }

            Object[] filters = excludeDefaultServiceFilters ? serviceFilters : ArrayUtils.addAll(serviceFilters, registrySettings.getDefaultServiceFilters());

            Pagination pagination = getPagination(defaultPagination, service, repository,
                registrySettings);

            ResourcePathsProvider defaultSparseFields = sparseFieldsDefaultsProvider;
            if (defaultSparseFields == null) {
                defaultSparseFields = registrySettings.getDefaultSparseFieldsProvider();
            }

            ResourcePathsProvider restrictedFields = restrictedFieldsProvider;
            if (restrictedFields == null) {
                restrictedFields = registrySettings.getDefaultRestrictedFieldsProvider();
            }

            CreateStrategy createStrategy = nvl(this.createStrategy, mapping.getCreateStrategy());
            UpdateStrategy updateStrategy = nvl(this.updateStrategy, mapping.getUpdateStrategy());
            DeleteStrategy deleteStrategy = nvl(this.deleteStrategy, mapping.getDeleteStrategy());

            BasicResource<T, ID> resource = new BasicResource(type, name, pluralName, basePath, registry, mapping, identityField, controllerMethodAccess, serviceMethodAccess,
                pagination, defaultSparseFields, restrictedFields, createStrategy,
                updateStrategy, deleteStrategy);
            Object service = this.service;
            Object repository = this.repository;
            if (service == null) {
                if (repository == null && registrySettings != null) {
                    RepositoryFactory factory = registrySettings.getRepositoryFactory();
                    if (factory != null) {
                        repository = factory.getRepository(resource);
                    }
                }
                Assert.notNull(repository, "operations and service may not both be null.");

                service = new FilteredService(resource, repository, filters);
            }

            ResourceServiceOperations resourceServiceOperations = null;
            if (service instanceof ResourceServiceOperations) {
                resourceServiceOperations = (ResourceServiceOperations) service;
            } else {
                resourceServiceOperations = new AnnotatedService(resource, service);
            }

            ResourceService<T, ID> resourceService = null;
            if (service instanceof ResourceService) {
                resourceService = (ResourceService) service;
            } else {
                resourceService = new DelegatingResourceService(resourceServiceOperations);
            }

            resource.setService(resourceService);
            resource.setServiceOperations(resourceServiceOperations);
            resource.setRepositoryOperations(toResourceRepository(resource, repository));
            return resource;
        }

        private ResourceRepositoryOperations toResourceRepository(Resource<?, ?> resource, Object repository) {
            if (repository == null) {
                return null;
            } else if (repository instanceof ResourceRepositoryOperations) {
                return (ResourceRepositoryOperations) repository;
            } else {
                return new AnnotatedResourceRepository(resource, repository);
            }
        }

    }

}
