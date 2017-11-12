package com.github.restup.service;

import com.github.restup.service.model.request.*;
import com.github.restup.service.model.response.PersistenceResult;
import com.github.restup.service.model.response.ReadResult;

import java.io.Serializable;
import java.util.List;

/**
 * Executes annotated methods for a {@link com.github.restup.repository.ResourceRepository}
 * or {@link ResourceService}
 *
 * @param <T>
 * @param <ID>
 */
public abstract class MethodCommandOperations<T, ID extends Serializable> {

    private final MethodCommand<PersistenceResult<T>> create;
    private final MethodCommand<PersistenceResult<T>> update;
    private final MethodCommand<PersistenceResult<T>> delete;
    private final MethodCommand<PersistenceResult<List<PersistenceResult<T>>>> bulkCreate;
    private final MethodCommand<PersistenceResult<List<PersistenceResult<T>>>> bulkUpdate;
    private final MethodCommand<PersistenceResult<List<PersistenceResult<T>>>> bulkDelete;
    private final MethodCommand<PersistenceResult<List<PersistenceResult<T>>>> updateByQuery;
    private final MethodCommand<PersistenceResult<List<PersistenceResult<T>>>> deleteByQuery;
    private final MethodCommand<ReadResult<List<T>>> list;
    private final MethodCommand<ReadResult<T>> find;

    public MethodCommandOperations(MethodCommandOperationFactory factory) {
        create = factory.getCreateOperation();
        update = factory.getUpdateOperation();
        delete = factory.getDeleteOperation();
        bulkCreate = factory.getBulkCreateOperation();
        bulkUpdate = factory.getBulkUpdateOperation();
        bulkDelete = factory.getBulkDeleteOperation();
        updateByQuery = factory.getUpdateByQueryOperation();
        deleteByQuery = factory.getDeleteByQueryOperation();
        list = factory.getListOperation();
        find = factory.getFindOperation();
    }

    private <R> R execute(MethodCommand<R> cmd, Object... args) {
        return cmd.execute(args);
    }

    public PersistenceResult<T> create(CreateRequest<T> request) {
        return execute(create, request);
    }

    public PersistenceResult<T> update(UpdateRequest<T, ID> request) {
        return execute(update, request);
    }

    public PersistenceResult<T> delete(DeleteRequest<T, ID> request) {
        return execute(delete, request);
    }

    public PersistenceResult<List<PersistenceResult<T>>> create(BulkRequest<CreateRequest<T>> request) {
        return execute(bulkCreate, request);
    }

    public PersistenceResult<List<PersistenceResult<T>>> update(BulkRequest<UpdateRequest<T, ID>> request) {
        return execute(bulkUpdate, request);
    }

    public PersistenceResult<List<PersistenceResult<T>>> delete(BulkRequest<DeleteRequest<T, ID>> request) {
        return execute(bulkDelete, request);
    }

    public PersistenceResult<List<PersistenceResult<T>>> updateByQueryCriteria(UpdateRequest<T, ID> request) {
        return execute(updateByQuery, request);
    }

    public PersistenceResult<List<PersistenceResult<T>>> deleteByQueryCriteria(DeleteRequest<T, ID> request) {
        return execute(deleteByQuery, request);
    }

    public ReadResult<List<T>> list(ListRequest<T> request) {
        return execute(list, request);
    }

    public ReadResult<T> find(ReadRequest<T, ID> request) {
        return execute(find, request);
    }


}
