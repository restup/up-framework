package com.github.restup.service;

import com.github.restup.resource.operations.*;

import java.io.Serializable;

/**
 * Interface which provides a suggested approximation for resource service operations. Implementing the interface is not required and signature of these methods can vary when using annotations
 *
 * @author abuttaro
 */
public interface ResourceService<T, ID extends Serializable> extends CreatableResource<T>,
        ReadableResource<T, ID>, UpdatableResource<T, ID>, DeletableResource<T, ID>, ListableResource<T>
        , BulkCreatableResource<T>, BulkUpdatableResource<T, ID>, BulkDeletableResource<T, ID>
        , UpdatableByQueryResource<T, ID>, DeletableByQueryResource<T, ID> {

}
