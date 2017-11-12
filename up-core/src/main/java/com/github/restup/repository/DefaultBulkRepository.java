package com.github.restup.repository;

import java.io.Serializable;

/**
 * Provides simple, default implementation of bulk operations.
 * <p>
 * These implementations are written to be functional and correct, but not necessarily
 * to perform optimally.  Consider optimized implementation if necessary or desirable.
 *
 * @param <T>
 * @param <ID>
 */
public class DefaultBulkRepository<T, ID extends Serializable> {

    //TODO JavaxValidationFilter pass index
    //TODO
//
//
//	@BulkCreateResource
//	public List<PersistenceResult<T>> create(BulkRequest<CreateRequest<T>> request) {
//		List<CreateRequest<T>> data = request.getData();
//		if (CollectionUtils.isNotEmpty(data) ) {
//			// pre
//			for (CreateRequest itemRequest : data) {
//
//			}
//			// assert errors
//			// execute requests
//			List<PersistenceResult<T>> result = new ArrayList<>(data.size());
//			for (CreateRequest itemRequest : data) {
//
//			}
//
//			// post
//			for (CreateRequest itemRequest : data) {
//
//			}
//		}
//		return null;
//	}
//
//	@BulkUpdateResource
//	public List<PersistenceResult<T>> update(BulkRequest<UpdateRequest<T,ID>> request) {
//		//TODO
//		return null;
//	}
//
//
//	@BulkDeleteResource
//	public List<PersistenceResult<T>> delete(BulkRequest<DeleteRequest<T,ID>> request) {
//		//TODO
//		return null;
//	}
//
//
//	@DeleteResourceByQuery
//	public List<PersistenceResult<T>> deleteByQueryCriteria(DeleteRequest<T, ID> request) {
//		return null;
//	}
//
//	@UpdateResourceByQuery
//	public List<PersistenceResult<T>> updateByQueryCriteria(UpdateRequest<T, ID> request) {
//		return null;
//	}
}
