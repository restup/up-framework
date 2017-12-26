package com.github.restup.registry.settings;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Predicate;

import com.github.restup.mapping.MappedClass;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistryRepository;
import com.github.restup.registry.ResourceRelationship;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * A simple {@link ResourceRegistryRepository} using {@link IdentityHashMap}. This does not use thread safe collections. However, the expectation is that maps are initialized at startup and then are read only. Under these circumstances, this will be thread safe
 *
 * @author andy.buttaro
 */
class DefaultResourceRegistryRepository implements ResourceRegistryRepository {

    private final Map<String, Resource<?, ?>> resources;
    private final Map<Type, MappedClass<?>> mappings;
    
    private final Table<String, String, ResourceRelationship<?, ?, ?, ?>> relationships;

    DefaultResourceRegistryRepository() {
        resources = new HashMap<>();
        mappings = new IdentityHashMap<>();
        relationships = HashBasedTable.create();
    }

    public void addRelationship(Resource<?, ?> from, Resource<?, ?> to,
            ResourceRelationship<?, ?, ?, ?> relationship) {
        relationships.put(from.getName(), to.getName(), relationship);
    }

    @Override
    public Collection<ResourceRelationship<?,?,?,?>> getRelationships(String resourceName) {
        Map<String, ResourceRelationship<?, ?, ?, ?>> map = relationships.column(resourceName);
        return map == null ? Collections.emptySet() : map.values();
    }

    public ResourceRelationship<?, ?, ?, ?> getRelationship(String from, String to) {
        return relationships.get(from, to);
    }

    public MappedClass<?> getMappedClass(Type resourceClass) {
        return mappings.get(resourceClass);
    }

    public Collection<Resource<?, ?>> getResources() {
        return resources.values();
    }

    public boolean hasResource(String resourceName) {
        return null != getResource(resourceName);
    }

    public boolean hasResource(Class<?> resourceClass) {
        return null != getResource(resourceClass);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> Resource<T, ?> getResource(Class<T> resourceClass) {
		return (Resource) getResource(resourceClass, r->resourceClass == r.getType() );
    }

    public Resource<?, ?> getResource(String resourceName) {
		return resources.get(resourceName);
    }

    public Resource<?, ?> getResourceByPluralName(String pluralName) {
		return getResource(pluralName, r->pluralName.equals(r.getPluralName()));
    }

    private Resource<?, ?> getResource(Object value, Predicate<Resource<?,?>> filter) {
    		return value == null ? null : getResources()
    				.stream()
    				.filter(filter)
    				.findAny()
    				.orElse(null);
    }

    public void registerResource(Resource<?, ?> resource) {
        resources.put(resource.getName(), resource);
    }

    public boolean hasMapping(Type mappedClass) {
        return mappings.containsKey(mappedClass);
    }

    public void registerMappedClass(MappedClass<?> mappedClass) {
        mappings.put(mappedClass.getType(), mappedClass);
    }

}
