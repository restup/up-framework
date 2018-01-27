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

    @Override
    public void addRelationship(Resource<?, ?> from, Resource<?, ?> to,
            ResourceRelationship<?, ?, ?, ?> relationship) {
        relationships.put(from.getName(), to.getName(), relationship);
    }

    @Override
    public Collection<ResourceRelationship<?,?,?,?>> getRelationships(String resourceName) {
        Map<String, ResourceRelationship<?, ?, ?, ?>> map = relationships.column(resourceName);
        return map == null ? Collections.emptySet() : map.values();
    }

    @Override
    public ResourceRelationship<?, ?, ?, ?> getRelationship(String from, String to) {
        return relationships.get(from, to);
    }

    @Override
    public MappedClass<?> getMappedClass(Type resourceClass) {
        return mappings.get(resourceClass);
    }

    @Override
    public Collection<Resource<?, ?>> getResources() {
        return resources.values();
    }

    @Override
    public boolean hasResource(String resourceName) {
        return null != getResource(resourceName);
    }

    @Override
    public boolean hasResource(Class<?> resourceClass) {
        return null != getResource(resourceClass);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> Resource<T, ?> getResource(Class<T> resourceClass) {
		return (Resource) getResource(resourceClass, r->resourceClass == r.getType() );
    }

    @Override
    public Resource<?, ?> getResource(String resourceName) {
		return resources.get(resourceName);
    }

    @Override
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

    @Override
    public void registerResource(Resource<?, ?> resource) {
        resources.put(resource.getName(), resource);
    }

    @Override
    public boolean hasMapping(Type mappedClass) {
        return mappings.containsKey(mappedClass);
    }

    @Override
    public void registerMappedClass(MappedClass<?> mappedClass) {
        mappings.put(mappedClass.getType(), mappedClass);
    }

}
