package com.github.restup.repository;

/**
 * Repository operations which wrap annotated repository methods
 * with flexible signatures
 */
public interface ResourceRepositoryOperations {

    Object create(Object... args);

    Object update(Object... args);

    Object delete(Object... args);

    Object bulkCreate(Object... args);

    Object bulkUpdate(Object... args);

    Object bulkDelete(Object... args);

    Object updateByQueryCriteria(Object... args);

    Object deleteByQueryCriteria(Object... args);

    Object list(Object... args);

    Object find(Object... args);

}
