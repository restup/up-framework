package com.github.restup.repository;

import com.github.restup.resource.operations.DeletableByQueryResource;
import com.github.restup.resource.operations.UpdatableByQueryResource;
import java.io.Serializable;

/**
 * Optional interface for convenience or tagging complete repository implementations.
 */
public interface ResourceRepository<T, ID extends Serializable> extends Repository, CrudRepository<T, ID>,
        DeletableByQueryResource<T, ID>, UpdatableByQueryResource<T, ID> {

}
