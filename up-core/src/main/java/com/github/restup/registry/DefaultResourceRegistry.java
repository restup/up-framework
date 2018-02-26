package com.github.restup.registry;

import java.lang.reflect.Type;
import java.util.Collection;
import com.github.restup.mapping.MappedClass;
import com.github.restup.registry.settings.RegistrySettings;
import com.github.restup.util.Assert;

/**
 * A registry of application {@link Resource}s, containing a {@link Resource}, containing meta data, field mappings, repository, and service details for each registered each resource <p> A singleton instance exists for convenience, but it is possible to construct multiple {@link DefaultResourceRegistry}s instances if needed.
 *
 * @author andy.buttaro
 */
public class DefaultResourceRegistry implements ResourceRegistry {

    private final RegistrySettings settings;
    private final ResourceRegistryRepository registryRepository;

    public DefaultResourceRegistry() {
        this(RegistrySettings.builder().build());
    }

    public DefaultResourceRegistry(RegistrySettings.Builder settings) {
        this(settings.build());
    }

    public DefaultResourceRegistry(RegistrySettings settings) {
        Assert.notNull(settings, "settings is required");
        Assert.notNull(settings.getResourceRegistryRepository(), "resourceRegistryRepository is required");
        this.registryRepository = settings.getResourceRegistryRepository();
        this.settings = settings;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void registerResource(Resource.Builder b) {
        registerResource(b.registry(this).build());
    }

    @Override
    public RegistrySettings getSettings() {
        return settings;
    }

    @Override
    public Resource<?, ?> getResource(String resourceName) {
        return registryRepository.getResource(resourceName);
    }

    @Override
    public Resource<?, ?> getResourceByPluralName(String pluralName) {
        return registryRepository.getResourceByPluralName(pluralName);
    }

    @Override
    public <T> Resource<T, ?> getResource(Class<T> resourceClass) {
        return registryRepository.getResource(resourceClass);
    }

    @Override
    public Collection<Resource<?, ?>> getResources() {
        return registryRepository.getResources();
    }

    @Override
    public void registerResource(Resource<?, ?> resource) {
        registryRepository.registerResource(resource);
    }

    @Override
    public boolean hasResource(String resourceName) {
        return registryRepository.hasResource(resourceName);
    }

    @Override
    public boolean hasResource(Class<?> resourceClass) {
        return registryRepository.hasResource(resourceClass);
    }

    @Override
    public MappedClass<?> getMappedClass(Type resourceClass) {
        return registryRepository.getMappedClass(resourceClass);
    }

    @Override
    public boolean hasMappedClass(Class<?> mappedClass) {
        return registryRepository.hasMapping(mappedClass);
    }

    @Override
    public void registerMappedClass(MappedClass<?> mappedClass) {
        registryRepository.registerMappedClass(mappedClass);
    }

    @Override
    public ResourceRelationship<?, ?, ?, ?> getRelationship(String from, String to) {
        return registryRepository.getRelationship(from, to);
    }

    @Override
    public ResourceRelationship<?, ?, ?, ?> getRelationship(Resource<?, ?> from, Resource<?, ?> to) {
        return getRelationship(from.getName(), to.getName());
    }

    @Override
    public Collection<ResourceRelationship<?,?,?,?>> getRelationships(String resourceName) {
        return registryRepository.getRelationships(resourceName);
    }

}
