package com.github.restup.service;

import com.github.restup.resource.operations.*;

import java.io.Serializable;

/**
 * Interface for resource operations.
 *
 * @param <T>
 * @param <ID>
 * @author abuttaro
 */
public interface ResourceService<T, ID extends Serializable> extends CreatableResource<T, ID>,
        ReadableResource<T, ID>, UpdatableResource<T, ID>, DeletableResource<T, ID>, ListableResource<T, ID>
        , BulkCreatableResource<T, ID>, BulkUpdatableResource<T, ID>, BulkDeletableResource<T, ID>
        , UpdatableByQueryResource<T, ID>, DeletableByQueryResource<T, ID> {


}
