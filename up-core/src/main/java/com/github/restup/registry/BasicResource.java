package com.github.restup.registry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.github.restup.mapping.MappedClass;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.path.ResourcePath;
import com.github.restup.path.ResourcePathsProvider;
import com.github.restup.query.Pagination;
import com.github.restup.registry.settings.ControllerMethodAccess;
import com.github.restup.registry.settings.ServiceMethodAccess;
import com.github.restup.repository.ResourceRepositoryOperations;
import com.github.restup.service.ResourceService;
import com.github.restup.service.ResourceServiceOperations;
import com.github.restup.util.Assert;

/**
 * Resource meta data, defining resource name, type, service implementation, etc.
 */
class BasicResource<T, ID extends Serializable> implements Resource<T,ID> {

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
    private ResourceService<T, ID> service;

    BasicResource(Class<T> type, String name, String pluralName, String basePath, ResourceRegistry registry, MappedClass<T> mapping, MappedField<ID> identityField, ControllerMethodAccess controllerAccess, ServiceMethodAccess serviceAccess, Pagination pagination, ResourcePathsProvider defaultSparseFields, ResourcePathsProvider restrictedFields) {
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

    public Collection<ResourceRelationship<?,?,?,?>> getRelationships() {
        return registry.getRelationships(name);
    }

	public List<ResourceRelationship<?,?,?,?>> getRelationshipsTo() {
        List<ResourceRelationship<?,?,?,?>> result = new ArrayList<>();
        Collection<ResourceRelationship<?,?,?,?>> relationships = getRelationships();
        if (relationships != null) {
            for (ResourceRelationship<?,?,?,?> relationship : relationships) {
                if (relationship.isTo(this)) {
                    result.add(relationship);
                }
            }
        }
        return result;
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

    public ResourceService<T, ID> getService() {
        return service;
    }

    protected final void setService(ResourceService<T, ID> service) {
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

}
