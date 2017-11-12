package com.github.restup.service;

import com.github.restup.service.model.response.PersistenceResult;
import com.github.restup.service.model.response.ReadResult;

import java.util.List;

/**
 * Provides operation specific {@link MethodCommand}s
 */
public interface MethodCommandOperationFactory {

    <T> MethodCommand<PersistenceResult<T>> getCreateOperation();

    <T> MethodCommand<PersistenceResult<T>> getUpdateOperation();

    <T> MethodCommand<PersistenceResult<T>> getDeleteOperation();

    <T> MethodCommand<PersistenceResult<List<PersistenceResult<T>>>> getBulkUpdateOperation();

    <T> MethodCommand<PersistenceResult<List<PersistenceResult<T>>>> getBulkCreateOperation();

    <T> MethodCommand<PersistenceResult<List<PersistenceResult<T>>>> getBulkDeleteOperation();

    <T> MethodCommand<PersistenceResult<List<PersistenceResult<T>>>> getDeleteByQueryOperation();

    <T> MethodCommand<PersistenceResult<List<PersistenceResult<T>>>> getUpdateByQueryOperation();

    <T> MethodCommand<ReadResult<List<T>>> getListOperation();

    <T> MethodCommand<ReadResult<T>> getFindOperation();
}
