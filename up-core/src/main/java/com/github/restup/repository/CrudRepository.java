package com.github.restup.repository;

import com.github.restup.resource.operations.CreatableResource;
import com.github.restup.resource.operations.DeletableResource;
import com.github.restup.resource.operations.ListableResource;
import com.github.restup.resource.operations.ReadableResource;
import com.github.restup.resource.operations.UpdatableResource;
import java.io.Serializable;

/**
 * Tag interface or interface for convenience for defining {@link CrudRepository}. <p> Usage is optional as a repository may use annotations without implementing {@link CrudRepository}
 */
public interface CrudRepository<T, ID extends Serializable> extends Repository, CreatableResource<T>,
        ReadableResource<T, ID>, UpdatableResource<T, ID>, DeletableResource<T, ID>, ListableResource<T> {

}
