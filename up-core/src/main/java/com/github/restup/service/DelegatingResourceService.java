package com.github.restup.service;

import com.github.restup.annotations.operations.*;
import com.github.restup.service.model.request.*;
import com.github.restup.service.model.response.PersistenceResult;
import com.github.restup.service.model.response.ReadResult;
import com.github.restup.service.model.response.ResourceResultConverterFactory;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * A {@link ResourceService} which wraps {@link ResourceServiceOperations} providing
 * typed method signatures for calling annotate methods with lenient signatures.
 *
 * Each wrapped method ensures the appropriate return type that is not necessarily
 * returned by the underlying annotated method.
 *
 * @param <T>
 * @param <ID>
 */
public class DelegatingResourceService<T, ID extends Serializable> implements ResourceService<T,ID> {

    private final ResourceServiceOperations delegate;

    public DelegatingResourceService(ResourceServiceOperations delegate) {
        this.delegate = delegate;
    }

    public <T> T result(Class<? extends Annotation> annotation, Object result) {
        return (T) ResourceResultConverterFactory.getInstance()
                .getConverter(annotation)
                .convert(result);
    }

    @Override
    public PersistenceResult<T> create(CreateRequest<T> request) {
        Object result = delegate.create(request);
        return result(CreateResource.class, result);
    }

    @Override
    public PersistenceResult<T> update(UpdateRequest<T, ID> request) {
        Object result = delegate.update(request);
        return result(UpdateResource.class, result);
    }

    @Override
    public PersistenceResult<T> delete(DeleteRequest<T, ID> request) {
        Object result = delegate.delete(request);
        return result(DeleteResource.class, result);
    }

    @Override
    public ReadResult<List<T>> list(ListRequest<T> request) {
        Object result = delegate.list(request);
        return result(ListResource.class, result);
    }

    @Override
    public ReadResult<T> find(ReadRequest<T, ID> request) {
        Object result = delegate.find(request);
        return result(ReadResource.class, result);
    }

    @Override
    public PersistenceResult<List<PersistenceResult<T>>> create(BulkRequest<CreateRequest<T>> request) {
        Object result = delegate.bulkCreate(request);
        return result(BulkCreateResource.class, result);
    }

    @Override
    public PersistenceResult<List<PersistenceResult<T>>> delete(BulkRequest<DeleteRequest<T, ID>> request) {
        Object result = delegate.bulkDelete(request);
        return result(BulkDeleteResource.class, result);
    }

    @Override
    public PersistenceResult<List<PersistenceResult<T>>> update(BulkRequest<UpdateRequest<T, ID>> request) {
        Object result = delegate.bulkUpdate(request);
        return result(BulkUpdateResource.class, result);
    }

    @Override
    public PersistenceResult<List<PersistenceResult<T>>> deleteByQueryCriteria(DeleteRequest<T, ID> request) {
        Object result = delegate.deleteByQueryCriteria(request);
        return result(DeleteResourceByQuery.class, result);
    }

    @Override
    public PersistenceResult<List<PersistenceResult<T>>> updateByQueryCriteria(UpdateRequest<T, ID> request) {
        Object result = delegate.updateByQueryCriteria(request);
        return result(UpdateResourceByQuery.class, result);
    }

}
