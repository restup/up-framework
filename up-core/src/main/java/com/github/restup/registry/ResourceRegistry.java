package com.github.restup.registry;

import com.github.restup.mapping.MappedClass;
import com.github.restup.mapping.MappedClassFactory;
import com.github.restup.registry.settings.RegistrySettings;
import com.github.restup.util.Assert;
import java.util.Collection;

/**
 * A registry of application {@link Resource}s, containing a {@link Resource}, containing meta data, field mappings, repository, and service details for each registered each resource <p> A singleton instance exists for convenience, but it is possible to construct multiple {@link ResourceRegistry}s instances if needed.
 *
 * @author andy.buttaro
 */
public final class ResourceRegistry implements MappedClassFactory {

    private static volatile ResourceRegistry instance = null;

    private final RegistrySettings settings;
    private final ResourceRegistryRepository resourceRegistryMap;
    private final MappedClassFactory mappedClassFactory;

    public ResourceRegistry() {
        this(RegistrySettings.builder().build());
    }

    public ResourceRegistry(RegistrySettings.Builder settings) {
        this(settings.build());
    }

    public ResourceRegistry(RegistrySettings settings) {
        Assert.notNull(settings, "settings is required");
        Assert.notNull(settings.getResourceRegistryMap(), "resourceRegistryMap is required");
        Assert.notNull(settings.getMappedClassFactory(), "mappedClassFactory is required");
        this.resourceRegistryMap = settings.getResourceRegistryMap();
        this.mappedClassFactory = settings.getMappedClassFactory();
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

    public void registerResource(Class<?>... resourceClasses) {
        for (Class<?> resourceClass : resourceClasses) {
            registerResource(Resource.builder(resourceClass));
        }
    }

    public RegistrySettings getSettings() {
        return settings;
    }

    public Resource<?, ?> getResource(String resourceName) {
        return resourceRegistryMap.getResource(resourceName);
    }

    public Resource<?, ?> getResourceByPluralName(String pluralName) {
        return resourceRegistryMap.getResourceByPluralName(pluralName);
    }

    public <T> Resource<T, ?> getResource(Class<T> resourceClass) {
        return resourceRegistryMap.getResource(resourceClass);
    }

    public Collection<Resource<?, ?>> getResources() {
        return resourceRegistryMap.getResources();
    }

    public void registerResource(Resource<?, ?> resource) {
        resourceRegistryMap.registerResource(resource);
    }

    public boolean hasResource(String resourceName) {
        return resourceRegistryMap.hasResource(resourceName);
    }

    public boolean hasResource(Class<?> resourceClass) {
        return resourceRegistryMap.hasResource(resourceClass);
    }

    public <T> MappedClass<T> getMappedClass(Class<T> resourceClass) {
        return resourceRegistryMap.getMappedClass(resourceClass);
    }

    public boolean isMappable(Class<?> type) {
        return mappedClassFactory.isMappable(type);
    }

    public boolean hasMappedClass(Class<?> mappedClass) {
        return resourceRegistryMap.hasMappedClass(mappedClass);
    }

    public void registerMappedClass(MappedClass<?> mappedClass) {
        resourceRegistryMap.registerMappedClass(mappedClass);
    }

    public ResourceRelationship<?, ?, ?, ?> getRelationship(String from, String to) {
        return resourceRegistryMap.getRelationship(from, to);
    }

    public ResourceRelationship<?, ?, ?, ?> getRelationship(Resource<?, ?> from, Resource<?, ?> to) {
        return getRelationship(from.getName(), to.getName());
    }

    public Collection<ResourceRelationship<?,?,?,?>> getRelationships(String resourceName) {
        return resourceRegistryMap.getRelationships(resourceName);
    }
}
