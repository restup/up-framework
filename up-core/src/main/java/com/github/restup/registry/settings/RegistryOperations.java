package com.github.restup.registry.settings;

import com.github.restup.mapping.MappedClass;
import com.github.restup.mapping.MappedClassFactory;
import com.github.restup.mapping.PolymorphicMappedClass;
import com.github.restup.mapping.fields.IterableField;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.path.ResourcePath;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistryRepository;
import com.github.restup.registry.ResourceRelationship;
import com.github.restup.util.Assert;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.Map.Entry;

/**
 * Registry implementation of {@link MappedClassFactory} and {@link ResourceRegistryRepository}
 * allowing implementation to be shared with other Up! implementations without reference to registry itself
 */
class RegistryOperations implements MappedClassFactory, ResourceRegistryRepository {

    private final ResourceRegistryRepository resourceRepository;
    private final MappedClassFactory mappedClassFactory;
    private Map<Object, List<Resource<?, ?>>> unknown;


    RegistryOperations(ResourceRegistryRepository resourceRegistryMap, MappedClassFactory mappedClassFactory) {
        this.resourceRepository = resourceRegistryMap;
        this.mappedClassFactory = mappedClassFactory;
        unknown = new HashMap<Object, List<Resource<?, ?>>>(3);
    }

    public Resource<?, ?> getResource(String resourceName) {
        return StringUtils.isEmpty(resourceName) ? null : resourceRepository.getResource(resourceName);
    }

    public Resource<?, ?> getResourceByPluralName(String pluralName) {
        return StringUtils.isEmpty(pluralName) ? null : resourceRepository.getResourceByPluralName(pluralName);
    }

    public <T> Resource<T, ?> getResource(Class<T> resourceClass) {
        return resourceClass == null ? null : resourceRepository.getResource(resourceClass);
    }

    public <T> MappedClass<T> getMappedClass(Class<T> resourceClass) {
        MappedClass<T> result = null;
        if (resourceClass != null) {
            result = resourceRepository.getMappedClass(resourceClass);
            if (result == null) {
                result = mappedClassFactory.getMappedClass(resourceClass);
                registerMappedClass(result);
            }

        }
        return result;
    }

    public boolean hasResource(Class<?> resourceClass) {
        return resourceClass == null ? false : resourceRepository.hasResource(resourceClass);
    }

    public boolean hasResource(String resourceName) {
        return StringUtils.isEmpty(resourceName) ? false : resourceRepository.hasResource(resourceName);
    }

    public boolean hasMappedClass(Class<?> mappedClass) {
        return mappedClass == null ? false : resourceRepository.hasMappedClass(mappedClass);
    }

    public boolean isMappable(Class<?> type) {
        return type == null ? false : mappedClassFactory.isMappable(type);
    }

    public synchronized void registerMappedClass(MappedClass<?> mappedClass) {
        if (mappedClass != null) {
            resourceRepository.registerMappedClass(mappedClass);
            mapGraph(mappedClass);
        }
    }

    public synchronized void registerResource(Resource<?, ?> resource) {
        Assert.notNull(resource, "resource is required");
        Assert.notNull(resource.getName(), "resource name must not be null");
        Assert.notNull(resource.getType(), "resource type must not be null");
        Assert.notNull(resource.getService(), "resource service must not be null");
        Assert.notNull(resource.getControllerAccess(), "resource httpAccess must not be null");
        Assert.notNull(resource.getServiceAccess(), "resource internalAccess must not be null");
        Assert.notNull(resource.getIdentityField(), "resource identityField must not be null");
        Assert.notNull(resource.getMapping(), "resource mapping must not be null");
        resourceRepository.registerResource(resource);
        registerMappedClass(resource.getMapping());

        // build relationships defined by this resource
        buildRelationships(resource);
        // check for and build relationshps defined from this resource
        List<Resource<?, ?>> from = removeMissingResourceRelationship(resource.getType());
        buildRelationships(from, resource);
    }

    private void buildRelationships(List<Resource<?, ?>> fromList, Resource<?, ?> to) {
        if (fromList != null) {
            for (Resource<?, ?> from : fromList) {
                Map<Class<?>, List<ResourcePath>> relationshipPaths = mapRelationships(from);
                List<ResourcePath> paths = relationshipPaths.get(to.getType());
                addRelationship(from, to, paths);
            }
        }
    }

    private void buildRelationships(Resource<?, ?> from) {
        Map<Class<?>, List<ResourcePath>> relationshipPaths = mapRelationships(from);
        for (Entry<Class<?>, List<ResourcePath>> e : relationshipPaths.entrySet()) {
            Resource<?, ?> to = getResource(e.getKey());
            if (to == null) {
                // hopefully this is a temporary condition at startup
                // where the registry is not yet aware of a resource.
                // we will hold the relationship for future,
                // hopefully resolving all misses... if not
                // consequence is we hold on to this and cannot offer support
                // for fetching relationships automatically (validation, includes)
                addMissingResourceRelationship(e.getKey(), from);
            } else {
                addRelationship(from, to, e.getValue());
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addRelationship(Resource<?, ?> from, Resource<?, ?> to, List<ResourcePath> fromPaths) {
        ResourceRelationship<?, ?, ?, ?> relationship
                = new ResourceRelationship(from, to, fromPaths);
        // add relationship in both direction
        addRelationship(from, to, relationship);
        addRelationship(to, from, relationship);
    }

    @Override
    public ResourceRelationship<?, ?, ?, ?> getRelationship(String from, String to) {
        return resourceRepository.getRelationship(from, to);
    }

    public void addRelationship(Resource<?, ?> from, Resource<?, ?> to,
                                ResourceRelationship<?, ?, ?, ?> relationship) {
        resourceRepository.addRelationship(from, to, relationship);
    }

    @Override
    public Collection<ResourceRelationship> getRelationships(String resourceName) {
        return resourceRepository.getRelationships(resourceName);
    }

    /**
     * Since a relationship to a resource can exist at multiple paths,
     * we map paths by resource class
     *
     * @param resource
     * @return
     */
    private Map<Class<?>, List<ResourcePath>> mapRelationships(Resource<?, ?> resource) {
        Map<Class<?>, List<ResourcePath>> relationshipsToClass = new HashMap<Class<?>, List<ResourcePath>>();
        List<ResourcePath> relationships = ResourceRelationship.getAllRelationshipPaths(resource);
        for (ResourcePath path : relationships) {
            Class<?> clazz = getRelationship(path);
            List<ResourcePath> paths = relationshipsToClass.get(clazz);
            if (paths == null) {
                paths = new ArrayList<ResourcePath>();
                relationshipsToClass.put(clazz, paths);
            }
            paths.add(path);
        }
        return relationshipsToClass;
    }

    private Class<?> getRelationship(ResourcePath path) {
        MappedField<?> mf = path.lastMappedField();
        if (mf != null) {
            return mf.getRelationshipResource();
        }
        return null;
    }

    private List<Resource<?, ?>> removeMissingResourceRelationship(Object to) {
        synchronized (unknown) {
            return unknown.remove(to);
        }
    }

    private void addMissingResourceRelationship(Object to, Resource<?, ?> resource) {
        synchronized (unknown) {
            List<Resource<?, ?>> list = unknown.get(to);
            if (list == null) {
                list = new ArrayList<Resource<?, ?>>();
                unknown.put(to, list);
            }
            list.add(resource);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void mapGraph(MappedClass<?> mapping) {
        // map parent type
        mapGraph(mapping.getParentType());
        // map sub types (if known)
        if (mapping instanceof PolymorphicMappedClass) {
            List<Class<?>> subtypes = ((PolymorphicMappedClass) mapping).getSubTypes();
            for (Class<?> type : subtypes) {
                mapGraph(type);
            }
        }
        // map embedded types
        for (MappedField<?> mappedField : mapping.getAttributes()) {
            mapGraph(mappedField.getType());
            if (mappedField instanceof IterableField) {
                mapGraph(((IterableField) mappedField).getGenericType());
            }
        }
    }

    private void mapGraph(Class<?> type) {
        if (mappedClassFactory.isMappable(type)) {
            if (!hasMappedClass(type)) {
                MappedClass<?> embeddedMappedClass = mappedClassFactory.getMappedClass(type);
                registerMappedClass(embeddedMappedClass);
            }
        }
    }

}
