package com.github.restup.registry.settings;

import com.github.restup.mapping.MappedClass;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistryRepository;
import com.github.restup.registry.ResourceRelationship;

import java.util.*;

/**
 * A simple {@link ResourceRegistryRepository} using {@link IdentityHashMap}. This does not use
 * thread safe collections. However, the expectation is that maps are
 * initialized at startup and then are read only. Under these circumstances,
 * this will be thread safe
 *
 * @author andy.buttaro
 */
class DefaultResourceRegistryRepository implements ResourceRegistryRepository {

    private final Map<Class<?>, Resource<?, ?>> resources;
    private final Map<Class<?>, MappedClass<?>> mappings;
    private final Map<String, Map<String, ResourceRelationship<?, ?, ?, ?>>> relationships;

    DefaultResourceRegistryRepository() {
        resources = new IdentityHashMap<Class<?>, Resource<?, ?>>();
        mappings = new IdentityHashMap<Class<?>, MappedClass<?>>();
        relationships = new HashMap<String, Map<String, ResourceRelationship<?, ?, ?, ?>>>();
    }

    public void addRelationship(Resource<?, ?> from, Resource<?, ?> to,
                                ResourceRelationship<?, ?, ?, ?> relationship) {
        Map<String, ResourceRelationship<?, ?, ?, ?>> map = relationships.get(from.getName());
        if (map == null) {
            map = new HashMap<String, ResourceRelationship<?, ?, ?, ?>>();
            relationships.put(from.getName(), map);
        }
        map.put(to.getName(), relationship);
    }

    @Override
    public Collection<ResourceRelationship> getRelationships(String resourceName) {
        Map<String, ResourceRelationship<?, ?, ?, ?>> map = relationships.get(resourceName);
        return map == null ? (Collection) Collections.emptySet() : (Collection) map.values();
    }

    public ResourceRelationship<?, ?, ?, ?> getRelationship(String from, String to) {
        Map<String, ResourceRelationship<?, ?, ?, ?>> relations = relationships.get(from);
        return relations == null ? null : relations.get(to);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> Resource<T, ?> getResource(Class<T> resourceClass) {
        return (Resource) resources.get(resourceClass);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> MappedClass<T> getMappedClass(Class<T> resourceClass) {
        return (MappedClass) mappings.get(resourceClass);
    }

    public Collection<Resource<?,?>> getResources() {
        return resources.values();
    }

    public boolean hasResource(String resourceName) {
        return null != getResource(resourceName);
    }

    public boolean hasResource(Class<?> resourceClass) {
        return null != getResource(resourceClass);
    }

    public Resource<?, ?> getResource(String resourceName) {
        if (resourceName != null) {
            for (Resource<?, ?> resource : resources.values()) {
                if (resourceName.equals(resource.getName())) {
                    return resource;
                }
            }
        }
        return null;
    }

    public Resource<?, ?> getResourceByPluralName(String pluralName) {
        if (pluralName != null) {
            for (Resource<?, ?> resource : resources.values()) {
                if (pluralName.equals(resource.getPluralName())) {
                    return resource;
                }
            }
        }
        return null;
    }

    public void registerResource(Resource<?, ?> resource) {
        resources.put(resource.getType(), resource);
    }

    public boolean hasMappedClass(Class<?> mappedClass) {
        return mappings.containsKey(mappedClass);
    }

    public void registerMappedClass(MappedClass<?> mappedClass) {
        mappings.put(mappedClass.getType(), mappedClass);
    }

}
