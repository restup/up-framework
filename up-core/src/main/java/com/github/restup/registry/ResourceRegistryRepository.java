package com.github.restup.registry;

import com.github.restup.mapping.MappedClass;
import java.util.Collection;

/**
 * Interface for all registry storage operations, allowing for the {@link ResourceRegistry} to store Resource meta data alternatively if needed
 *
 * @author andy.buttaro
 */
public interface ResourceRegistryRepository {

    /**
     * @param resourceName the name of the resource to find
     * @return the resource with name equal to the passed resourceName, null otherwise
     */
    Resource<?, ?> getResource(String resourceName);

    /**
     * @param pluralName the pluralName of the resource to find
     * @return the resource with pluralName equal to the passed pluralName, null otherwise
     */
    Resource<?, ?> getResourceByPluralName(String pluralName);

    /**
     * @param resourceClass the class of the resource to find
     * @return the resource with type equal to the passed resourceClass, null otherwise
     */
    <T> Resource<T, ?> getResource(Class<T> resourceClass);

    /**
     * Add the resource to the {@link ResourceRegistryRepository}
     */
    void registerResource(Resource<?, ?> resource);

    /**
     * Add the MappedClass to the {@link ResourceRegistryRepository}
     */
    void registerMappedClass(MappedClass<?> mappedClass);

    /**
     * @return true if a resource with a name matching resourceName exists, false otherwise
     */
    boolean hasResource(String resourceName);

    /**
     * @return true if a resource with a class matching resourceClass exists, false otherwise
     */
    boolean hasResource(Class<?> resourceClass);

    /**
     * @return a mappedClass, never null
     */
    <T> MappedClass<T> getMappedClass(Class<T> mappedClass);

    /**
     * @return true if a mappedClass with a class matching mappedClass exists, false otherwise
     */
    boolean hasMappedClass(Class<?> mappedClass);

    /**
     * add a relationship between two objects
     *
     * @param relationship between from an to
     */
    void addRelationship(Resource<?, ?> from, Resource<?, ?> to, ResourceRelationship<?, ?, ?, ?> relationship);

    /**
     * @return relationship between 2 resources or null if none
     */
    ResourceRelationship<?, ?, ?, ?> getRelationship(String from, String to);

    /**
     * @param resourceName
     * @return
     */
    Collection<ResourceRelationship<?,?,?,?>> getRelationships(String resourceName);

    Collection<Resource<?, ?>> getResources();

}
