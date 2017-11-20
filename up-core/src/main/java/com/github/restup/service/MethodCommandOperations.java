package com.github.restup.service;

import com.github.restup.service.model.request.*;

import java.io.Serializable;

/**
 * Executes annotated methods for a {@link com.github.restup.repository.ResourceRepository}
 * or {@link ResourceService}
 */
public abstract class MethodCommandOperations {

    private final MethodCommand create;
    private final MethodCommand update;
    private final MethodCommand delete;
    private final MethodCommand bulkCreate;
    private final MethodCommand bulkUpdate;
    private final MethodCommand bulkDelete;
    private final MethodCommand updateByQuery;
    private final MethodCommand deleteByQuery;
    private final MethodCommand list;
    private final MethodCommand find;

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

    public Object create(Object... args) {
        return execute(create, args);
    }

    public Object update(Object... args) {
        return execute(update, args);
    }

    public Object delete(Object... args) {
        return execute(delete, args);
    }

    public Object bulkCreate(Object... args) {
        return execute(bulkCreate, args);
    }

    public Object bulkUpdate(Object... args) {
        return execute(bulkUpdate, args);
    }

    public Object bulkDelete(Object... args) {
        return execute(bulkDelete, args);
    }

    public Object updateByQueryCriteria(Object... args) {
        return execute(updateByQuery, args);
    }

    public Object deleteByQueryCriteria(Object... args) {
        return execute(deleteByQuery, args);
    }

    public Object list(Object... args) {
        return execute(list, args);
    }

    public Object find(Object... args) {
        return execute(find, args);
    }


}
