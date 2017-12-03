package com.github.restup.registry;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.github.restup.mapping.MappedClass;
import com.github.restup.mapping.MappedClassFactory;
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

/**
 * Resource meta data, defining resource name, type, service implementation, etc.
 *
 * @param <T>
 * @param <ID>
 */
public class Resource<T, ID extends Serializable> implements Comparable<Resource> {

    private final Class<T> type;
    private final String name;
    private final String pluralName;
    private final String basePath;

    private final ResourceRegistry registry;
    private final MappedClass<T> mapping;
    private final MappedField<ID> identityField;

    private final ControllerMethodAccess controllerAccess;
    private final ServiceMethodAccess serviceAccess;
    private final Pagination defaultPagination;
    private final ResourcePathsProvider defaultSparseFields;
    private final ResourcePathsProvider restrictedFields;
    private ResourceServiceOperations serviceOperations;
    private ResourceRepositoryOperations repositoryOperations;
    private ResourceService<T,ID> service;

    public Resource(Class<T> type, String name, String pluralName, String basePath, ResourceRegistry registry, MappedClass<T> mapping, MappedField<ID> identityField, ControllerMethodAccess controllerAccess, ServiceMethodAccess serviceAccess, Pagination pagination, ResourcePathsProvider defaultSparseFields, ResourcePathsProvider restrictedFields) {
        Assert.notNull(type, "type is required");
        Assert.notNull(name, "name is required");
        Assert.notNull(pluralName, "pluralName is required");
        Assert.notNull(basePath, "basePath is required");
        Assert.notNull(registry, "registry is required");
        Assert.notNull(mapping, "mapping is required");
        Assert.notNull(controllerAccess, "controllerAccess is required");
        Assert.notNull(serviceAccess, "serviceAccess is required");
        Assert.notNull(pagination, "pagination is required");
        Assert.notNull(defaultSparseFields, "defaultSparseFields is required");
        Assert.notNull(restrictedFields, "restrictedFields is required");
        this.type = type;
        this.name = name;
        this.basePath = basePath;
        this.pluralName = pluralName;
        this.registry = registry;
        this.mapping = mapping;
        this.identityField = identityField;
        this.controllerAccess = controllerAccess;
        this.serviceAccess = serviceAccess;
        this.defaultPagination = pagination;
        this.defaultSparseFields = defaultSparseFields;
        this.restrictedFields = restrictedFields;
    }

    /**
     * Ensures basepath always has leading and trailing / so it is prepared
     * for url concatenation
     *
     * @param basePath
     * @return
     */
    public static String cleanBasePath(String basePath) {
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static List<ResourcePath> getAllPaths(Resource<?, ?> resource) {
        return resource == null ? (List) Collections.emptyList() : resource.getAllPaths();
    }

    public static <T, ID extends Serializable> Set<ID> getIds(Resource<T, ID> resource, List<T> list) {
        Set<ID> result;
        int size = CollectionUtils.size(list);
        if (size < 1) {
            result = Collections.emptySet();
        } else {
            result = (Set) list.stream().map(t -> resource.getIdentityField().readValue(t))
            		.collect(Collectors.toSet());
        }
        return result;
    }

    public static List<ResourcePath> getPaths(Resource<?, ?> resource, boolean includeTransient, boolean apiFieldsOnly) {
        //TODO better to cache immutable paths?
        List<ResourcePath> paths = new ArrayList<ResourcePath>();
        for (MappedField<?> mf : resource.getMapping().getAttributes()) {
            if (!includeTransient) {
                if (mf.isTransientField()) {
                    continue;
                }
            }
            if (apiFieldsOnly) {
                if (!mf.isApiProperty()) {
                    continue;
                }
            }
            paths.add(ResourcePath.path(resource, mf));
        }
        return paths;
    }

    public static <T, ID extends Serializable> Builder<T, ID> builder(Class<T> resourceClass) {
        return new Builder<T, ID>(resourceClass);
    }

    public static <T, ID extends Serializable> Builder<T, ID> builder(Class<T> resourceClass, Class<ID> idClass) {
        return new Builder<T, ID>(resourceClass);
    }

    public MappedField<?> findApiField(String field) {
        return ResourcePath.findApiField(mapping, field);
    }

    public MappedField<?> findPersistedField(String field) {
        return ResourcePath.findPersistedField(mapping, field);
    }

    public MappedField<?> findBeanField(String field) {
        return ResourcePath.findBeanField(mapping, field);
    }

    public boolean hasApiField(String field) {
        return findApiField(field) != null;
    }

    public boolean hasPersistedField(String field) {
        return findPersistedField(field) != null;
    }

    public boolean hasBeanField(String field) {
        return findBeanField(field) != null;
    }

    public List<ResourcePath> getAllPaths() {
        //TODO better to cache immutable paths?
        List<ResourcePath> paths = new ArrayList<ResourcePath>();
        appendPaths(paths, mapping, null);
        return paths;
    }

    private void appendPaths(List<ResourcePath> target, MappedClass<?> mapping, ResourcePath parent) {
        //TODO better to cache immutable paths?
        for (MappedField<?> mf : mapping.getAttributes()) {
            ResourcePath path = parent == null ? ResourcePath.path(this, mf) : ResourcePath.path(parent, mf);
            target.add(path);
            MappedClass<?> subPath = registry.getMappedClass(mf.getType());
            if (subPath != null) {
                appendPaths(target, subPath, path);
            }
        }
    }

    public List<ResourcePath> getPaths(boolean includeTransient) {
        return getPaths(this, includeTransient, false);
    }

    public Class<T> getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getPluralName() {
        return pluralName;
    }

    public MappedClass<T> getMapping() {
        return mapping;
    }

    public MappedField<ID> getIdentityField() {
        return identityField;
    }

    public ResourceService<T,ID> getService() {
        return service;
    }

    protected final void setService(ResourceService<T,ID> service) {
        Assert.isNull(this.service, "service is immutable");
        this.service = service;
    }

    public ResourceServiceOperations getServiceOperations() {
        return serviceOperations;
    }

    protected final void setServiceOperations(ResourceServiceOperations serviceOperations) {
        Assert.isNull(this.serviceOperations, "serviceOperations is immutable");
        this.serviceOperations = serviceOperations;
    }

    public ResourceRepositoryOperations getRepositoryOperations() {
        return repositoryOperations;
    }

    protected final void setRepositoryOperations(ResourceRepositoryOperations repositoryOperations) {
        Assert.isNull(this.repositoryOperations, "repositoryOperations is immutable");
        this.repositoryOperations = repositoryOperations;
    }

    public ControllerMethodAccess getControllerAccess() {
        return controllerAccess;
    }

    public ServiceMethodAccess getServiceAccess() {
        return serviceAccess;
    }

    public ResourceRegistry getRegistry() {
        return registry;
    }

    public Pagination getDefaultPagination() {
        return defaultPagination;
    }

    /**
     * @return the fields returned by default for sparse fields requests.
     */
    public List<ResourcePath> getDefaultSparseFields() {
        return defaultSparseFields.getPaths(this);
    }

    /**
     * Returns any fields to which the requestor is not permitted to read
     *
     * @return
     */
    public List<ResourcePath> getRestrictedFields() {
        return restrictedFields.getPaths(this);
    }

    public String getBasePath() {
        return basePath;
    }

    @Override
    public String toString() {
        return name;
    }

    public Collection<ResourceRelationship> getRelationships() {
        return registry.getRelationships(name);
    }

    public List<ResourceRelationship> getRelationshipsTo() {
        List<ResourceRelationship> result = new ArrayList<ResourceRelationship>();
        Collection<ResourceRelationship> relationships = getRelationships();
        if (relationships != null) {
            for (ResourceRelationship relationship : relationships) {
                if (relationship.isTo(this)) {
                    result.add(relationship);
                }
            }
        }
        return result;
    }

    public final static class Builder<T, ID extends Serializable> {

        private final Class<T> type;
        private ResourceRegistry registry;
        private Object repository;
        private Object service;
        private Object[] serviceFilters;
        private RegistrySettings settings;
        private boolean excludeDefaultServiceFilters;
        private ControllerMethodAccess controllerAccess;
        private ServiceMethodAccess serviceAccess;
        private String name;
        private String pluralName;
        private String basePath;
        private Pagination defaultPagination;
        private ResourcePathsProvider sparseFieldsDefaultsProvider;
        private ResourcePathsProvider restrictedFieldsProvider;
        private MappedClass mappedClass;
        private MappedClassFactory mappedClassFactory;

        public Builder(Class<T> resourceClass) {
            Assert.notNull(resourceClass, "resource class must not be null");
            this.type = resourceClass;
        }

        public Builder<T, ID> me() {
            return this;
        }

        public Builder<T, ID> registry(ResourceRegistry registry) {
            this.registry = registry;
            return me();
        }

        public Builder<T, ID> settings(RegistrySettings settings) {
            this.settings = settings;
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

        public Builder<T, ID> controllerAccess(ControllerMethodAccess controllerAccess) {
            this.controllerAccess = controllerAccess;
            return me();
        }

        public Builder<T, ID> serviceAccess(ServiceMethodAccess serviceAccess) {
            this.serviceAccess = serviceAccess;
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

        public Builder<T, ID> excludeDefaultServiceFilters(boolean excludeDefaultServiceFilters) {
            this.excludeDefaultServiceFilters = excludeDefaultServiceFilters;
            return me();
        }

        public Builder<T, ID> defaultPagination(Pagination defaultPagination) {
            this.defaultPagination = defaultPagination;
            return me();
        }

        public Builder<T, ID> restrictedFieldsProvider(ResourcePathsProvider restrictedFieldsProvider) {
            this.restrictedFieldsProvider = restrictedFieldsProvider;
            return me();
        }

        public Builder<T, ID> sparseFieldsDefaultsProvider(ResourcePathsProvider sparseFieldsDefaultsProvider) {
            this.sparseFieldsDefaultsProvider = sparseFieldsDefaultsProvider;
            return me();
        }

        /**
         * @param pageLimit
         * @param pageOffset
         * @param pagingDisabled     so that no paging information is returned and full result set is returned.
         * @param withTotalsDisabled so that last page cannot be determined
         * @return
         */
        public Builder<T, ID> defaultPagination(Integer pageLimit, Integer pageOffset, boolean pagingDisabled, boolean withTotalsDisabled) {
            return defaultPagination(new Pagination(pageLimit, pageOffset, pagingDisabled, withTotalsDisabled));
        }

        public Builder<T, ID> mappedClassFactory(MappedClassFactory mappedClassFactory) {
            this.mappedClassFactory = mappedClassFactory;
            return me();
        }

        public Builder<T, ID> mappedClass(MappedClass mappedClass) {
            this.mappedClass = mappedClass;
            return me();
        }

        public Builder<T, ID> mappedClass(MappedClass.Builder builder) {
            return mappedClass(builder.build());
        }

        /**
         * {@link #defaultPagination(Integer, Integer, boolean, boolean)} with offset of 0, paging and totals enabled.
         *
         * @param pageLimit
         * @return
         */
        public Builder<T, ID> defaultPagination(Integer pageLimit) {
            return defaultPagination(pageLimit, 0, false, false);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public Resource<T, ID> build() {
            Assert.notNull(type, "resource class must not be null");
            Assert.notNull(registry, "registry must not be null");

            RegistrySettings registrySettings = this.settings;
            if (registrySettings == null) {
                registrySettings = registry.getSettings();
            }

            ControllerMethodAccess controllerMethodAccess = this.controllerAccess;
            if (controllerMethodAccess == null) {
                controllerMethodAccess = registrySettings.getDefaultControllerAccess();
            }
            ServiceMethodAccess serviceMethodAccess = this.serviceAccess;
            if (serviceMethodAccess == null) {
                serviceMethodAccess = registrySettings.getDefaultServiceAccess();
            }

            MappedClass<T> mapping = this.mappedClass;
            if ( mapping == null ) {
                MappedClassFactory mappedClassFactory = this.mappedClassFactory;
                if (mappedClassFactory == null) {
                    mappedClassFactory = registrySettings.getMappedClassFactory();
                }
                Assert.notNull(mappedClassFactory, "mappedClassFactory must not be null");

                mapping = mappedClassFactory.getMappedClass(this.type);
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

            Object[] filters = excludeDefaultServiceFilters ? serviceFilters :
                    ArrayUtils.addAll(serviceFilters, registrySettings.getDefaultServiceFilters());

            Pagination pagination = defaultPagination;
            if (pagination == null) {
                pagination = registrySettings.getDefaultPagination();
            }

            ResourcePathsProvider defaultSparseFields = this.sparseFieldsDefaultsProvider;
            if (defaultSparseFields == null) {
                defaultSparseFields = registrySettings.getDefaultSparseFieldsProvider();
            }

            ResourcePathsProvider restrictedFields = this.restrictedFieldsProvider;
            if (restrictedFields == null) {
                restrictedFields = registrySettings.getDefaultRestrictedFieldsProvider();
            }

            Resource<T, ID> resource = new Resource<T, ID>(type, name, pluralName, basePath, registry, mapping, identityField, controllerMethodAccess, serviceMethodAccess, pagination, defaultSparseFields, restrictedFields);
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
            if (service instanceof ResourceServiceOperations){
                resourceServiceOperations = (ResourceServiceOperations) service;
            } else {
                resourceServiceOperations = new AnnotatedService(resource, service);
            }

            ResourceService<T,ID> resourceService = null;
            if ( service instanceof ResourceService ) {
                resourceService = (ResourceService) service;
            } else {
                resourceService = new DelegatingResourceService(resourceServiceOperations);
            }

            resource.setService(resourceService);
            resource.setServiceOperations(resourceServiceOperations);
            resource.setRepositoryOperations(toResourceRepository(resource, repository));
            return resource;
        }

        private ResourceRepositoryOperations toResourceRepository(Resource resource, Object repository) {
            if ( repository == null ) {
                return null;
            } else if ( repository instanceof  ResourceRepositoryOperations ) {
                return (ResourceRepositoryOperations) repository;
            } else {
                return new AnnotatedResourceRepository(resource, repository);
            }
        }

    }

    @Override
    public int compareTo(Resource o) {
        if ( o == null ) {
            return -1;
        }
        return getName().compareTo(o.getName());
    }

}
