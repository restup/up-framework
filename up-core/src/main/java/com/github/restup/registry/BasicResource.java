package com.github.restup.registry;

import com.github.restup.annotations.model.CreateStrategy;
import com.github.restup.annotations.model.DeleteStrategy;
import com.github.restup.annotations.model.UpdateStrategy;
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
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Resource meta data, defining resource name, type, service implementation, etc.
 */
class BasicResource<T, ID extends Serializable> implements Resource<T,ID> {

    private final Type type;
    private final String name;
    private final String pluralName;
    private final String basePath;

    private final ResourceRegistry registry;
    private final MappedClass<T> mapping;
    private final MappedField<ID> identityField;

    private final ControllerMethodAccess controllerMethodAccess;
    private final ServiceMethodAccess serviceMethodAccess;
    private final Pagination defaultPagination;
    private final ResourcePathsProvider defaultSparseFieldsProvider;
    private final ResourcePathsProvider restrictedFieldsProvider;
    private final CreateStrategy createStrategy;
    private final UpdateStrategy updateStrategy;
    private final DeleteStrategy deleteStrategy;
    private ResourceServiceOperations serviceOperations;
    private ResourceRepositoryOperations repositoryOperations;
    private ResourceService<T, ID> service;

    BasicResource(Type type, String name, String pluralName, String basePath,
        ResourceRegistry registry, MappedClass<T> mapping, MappedField<ID> identityField,
        ControllerMethodAccess controllerAccess, ServiceMethodAccess serviceAccess,
        Pagination pagination, ResourcePathsProvider defaultSparseFields,
        ResourcePathsProvider restrictedFields, CreateStrategy createStrategy,
        UpdateStrategy updateStrategy, DeleteStrategy deleteStrategy) {
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
        controllerMethodAccess = controllerAccess;
        serviceMethodAccess = serviceAccess;
        defaultPagination = pagination;
        defaultSparseFieldsProvider = defaultSparseFields;
        restrictedFieldsProvider = restrictedFields;
        this.createStrategy = createStrategy;
        this.updateStrategy = updateStrategy;
        this.deleteStrategy = deleteStrategy;
    }


    @Override
    public List<ResourcePath> getAllPaths() {
        //TODO better to cache immutable paths?
        List<ResourcePath> paths = new ArrayList<>();
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

    @Override
    public Collection<ResourceRelationship<?,?,?,?>> getRelationships() {
        return registry.getRelationships(name);
    }

	@Override
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

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPluralName() {
        return pluralName;
    }

    @Override
    public MappedClass<T> getMapping() {
        return mapping;
    }

    @Override
    public MappedField<ID> getIdentityField() {
        return identityField;
    }

    @Override
    public ResourceService<T, ID> getService() {
        return service;
    }

    protected final void setService(ResourceService<T, ID> service) {
        Assert.isNull(this.service, "service is immutable");
        this.service = service;
    }

    @Override
    public CreateStrategy getCreateStrategy() {
        return createStrategy;
    }

    @Override
    public UpdateStrategy getUpdateStrategy() {
        return updateStrategy;
    }

    @Override
    public DeleteStrategy getDeleteStrategy() {
        return deleteStrategy;
    }

    @Override
    public ResourceServiceOperations getServiceOperations() {
        return serviceOperations;
    }

    protected final void setServiceOperations(ResourceServiceOperations serviceOperations) {
        Assert.isNull(this.serviceOperations, "serviceOperations is immutable");
        this.serviceOperations = serviceOperations;
    }

    @Override
    public ResourceRepositoryOperations getRepositoryOperations() {
        return repositoryOperations;
    }

    protected final void setRepositoryOperations(ResourceRepositoryOperations repositoryOperations) {
        Assert.isNull(this.repositoryOperations, "repositoryOperations is immutable");
        this.repositoryOperations = repositoryOperations;
    }

    @Override
    public ControllerMethodAccess getControllerMethodAccess() {
        return controllerMethodAccess;
    }

    @Override
    public ServiceMethodAccess getServiceMethodAccess() {
        return serviceMethodAccess;
    }

    @Override
    public ResourceRegistry getRegistry() {
        return registry;
    }

    @Override
    public Pagination getDefaultPagination() {
        return defaultPagination;
    }

    /**
     * @return the fields returned by default for sparse fields requests.
     */
    @Override
    public List<ResourcePath> getDefaultSparseFields() {
        return defaultSparseFieldsProvider.getPaths(this);
    }

    public ResourcePathsProvider getDefaultSparseFieldsProvider() {
        return defaultSparseFieldsProvider;
    }

    /**
     * Returns any fields to which the requestor is not permitted to read
     */
    @Override
    public List<ResourcePath> getRestrictedFields() {
        return restrictedFieldsProvider.getPaths(this);
    }

    public ResourcePathsProvider getRestrictedFieldsProvider() {
        return restrictedFieldsProvider;
    }

    @Override
    public String getBasePath() {
        return basePath;
    }

    @Override
    public String toString() {
        return name;
    }

}
