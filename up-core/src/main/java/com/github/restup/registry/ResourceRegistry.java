package com.github.restup.registry;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;

import com.github.restup.mapping.MappedClass;
import com.github.restup.mapping.MappedClassRegistry;
import com.github.restup.registry.settings.RegistrySettings;
import com.github.restup.util.Assert;
import com.github.restup.util.Streams;

/**
 * A registry of application {@link Resource}s, containing a {@link Resource}, containing meta data, field mappings, repository, and service details for each registered each resource <p> A singleton instance exists for convenience, but it is possible to construct multiple {@link ResourceRegistry}s instances if needed.
 *
 * @author andy.buttaro
 */
public final class ResourceRegistry implements MappedClassRegistry {

    private static volatile ResourceRegistry instance = null;

    private final RegistrySettings settings;
    private final ResourceRegistryRepository registryRepository;

    public ResourceRegistry() {
        this(RegistrySettings.builder().build());
    }

    public ResourceRegistry(RegistrySettings.Builder settings) {
        this(settings.build());
    }

    public ResourceRegistry(RegistrySettings settings) {
        Assert.notNull(settings, "settings is required");
        Assert.notNull(settings.getResourceRegistryMap(), "resourceRegistryMap is required");
        this.registryRepository = settings.getResourceRegistryMap();
        this.settings = settings;
        // set singleton instance
        if (settings.isPrimaryRegistry() || instance == null) {
            instance = this;
        }
    }

    public static ResourceRegistry getInstance() {
        if (instance == null) {
            synchronized (ResourceRegistry.class) {
                if (instance == null) {
                    instance = new ResourceRegistry(new RegistrySettings.Builder());
                }
            }
        }
        return instance;
    }

    @SuppressWarnings("rawtypes")
    public void registerResource(Resource.Builder b) {
        registerResource(b.registry(this).build());
    }

    public void registerResource(Class<?> resourceClass) {
        registerResource(Resource.builder(resourceClass));
    }

    public void registerResource(Class<?>... resourceClasses) {
    		Streams.forEach(resourceClasses, this::registerResource);
    }

    public RegistrySettings getSettings() {
        return settings;
    }

    public Resource<?, ?> getResource(String resourceName) {
        return registryRepository.getResource(resourceName);
    }

    public Resource<?, ?> getResourceByPluralName(String pluralName) {
        return registryRepository.getResourceByPluralName(pluralName);
    }

    public <T> Resource<T, ?> getResource(Class<T> resourceClass) {
        return registryRepository.getResource(resourceClass);
    }

    public Collection<Resource<?, ?>> getResources() {
        return registryRepository.getResources();
    }

    public void registerResource(Resource<?, ?> resource) {
        registryRepository.registerResource(resource);
    }

    public boolean hasResource(String resourceName) {
        return registryRepository.hasResource(resourceName);
    }

    public boolean hasResource(Class<?> resourceClass) {
        return registryRepository.hasResource(resourceClass);
    }

    @Override
    public MappedClass<?> getMappedClass(Type resourceClass) {
        return registryRepository.getMappedClass(resourceClass);
    }

    public boolean hasMappedClass(Class<?> mappedClass) {
        return registryRepository.hasMapping(mappedClass);
    }

    public void registerMappedClass(MappedClass<?> mappedClass) {
        registryRepository.registerMappedClass(mappedClass);
    }

    public ResourceRelationship<?, ?, ?, ?> getRelationship(String from, String to) {
        return registryRepository.getRelationship(from, to);
    }

    public ResourceRelationship<?, ?, ?, ?> getRelationship(Resource<?, ?> from, Resource<?, ?> to) {
        return getRelationship(from.getName(), to.getName());
    }

    public Collection<ResourceRelationship<?,?,?,?>> getRelationships(String resourceName) {
        return registryRepository.getRelationships(resourceName);
    }
}
