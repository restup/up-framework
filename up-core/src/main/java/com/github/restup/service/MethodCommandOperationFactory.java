package com.github.restup.service;

/**
 * Provides operation specific {@link MethodCommand}s
 */
public interface MethodCommandOperationFactory {

    MethodCommand<?> getCreateOperation();

    MethodCommand<?> getUpdateOperation();

    MethodCommand<?> getDeleteOperation();

    MethodCommand<?> getBulkUpdateOperation();

    MethodCommand<?> getBulkCreateOperation();

    MethodCommand<?> getBulkDeleteOperation();

    MethodCommand<?> getDeleteByQueryOperation();

    MethodCommand<?> getUpdateByQueryOperation();

    MethodCommand<?> getListOperation();

    MethodCommand<?> getFindOperation();
}
