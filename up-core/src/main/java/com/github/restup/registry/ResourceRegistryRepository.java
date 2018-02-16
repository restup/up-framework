package com.github.restup.registry;

import java.lang.reflect.Type;
import java.util.Collection;
import com.github.restup.mapping.MappedClass;

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
     * @param <T> type of resource
     * @param resourceClass the class of the resource to find
     * @return the resource with type equal to the passed resourceClass, null otherwise
     */
    <T> Resource<T, ?> getResource(Class<T> resourceClass);

    /**
     * Add the resource to the {@link ResourceRegistryRepository}
     * 
     * @param resource to register
     */
    void registerResource(Resource<?, ?> resource);

    /**
     * Add the MappedClass to the {@link ResourceRegistryRepository}
     * 
     * @param mappedClass to register
     */
    void registerMappedClass(MappedClass<?> mappedClass);

    /**
     * 
     * @param resourceName to find
     * @return true if a resource with a name matching resourceName exists, false otherwise
     */
    boolean hasResource(String resourceName);

    /**
     * @param resourceClass to find
     * @return true if a resource with a class matching resourceClass exists, false otherwise
     */
    boolean hasResource(Class<?> resourceClass);

    /**
     * @param mappedClass to retrieve
     * @return a mappedClass
     */
    MappedClass<?> getMappedClass(Type mappedClass);

    /**
     * @param mapping to find
     * @return true if a mappedClass with a class matching mappedClass exists, false otherwise
     */
    boolean hasMapping(Type mapping);

    /**
     * add a relationship between two objects
     *
     * @param from resource
     * @param to resource
     * @param relationship between from and to
     */
    void addRelationship(Resource<?, ?> from, Resource<?, ?> to, ResourceRelationship<?, ?, ?, ?> relationship);

    /**
     * @return relationship between 2 resources or null if none
     * @param from resource
     * @param to resource
     */
    ResourceRelationship<?, ?, ?, ?> getRelationship(String from, String to);

    /**
     * @param resourceName of resource
     * @return relationships for resource
     */
    Collection<ResourceRelationship<?,?,?,?>> getRelationships(String resourceName);

    /**
     * 
     * @return all registered resources
     */
    Collection<Resource<?, ?>> getResources();

}
