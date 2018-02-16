package com.github.restup.controller.method;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.registry.Resource;
import com.github.restup.service.ResourceServiceOperations;
import com.github.restup.service.model.request.BulkRequest;
import com.github.restup.service.model.request.DeleteRequest;
import com.github.restup.service.model.request.RequestObjectFactory;

/**
 * Handle DELETE operations
 * <ul>
 * <li>Multiple documents</li>
 * <li>A single document</li>
 * <li>Multiple document matching query criteria</li>
 * </ul>
 * 
 * @param <T> resource type
 * @param <ID> resource id type
 */
public class DeleteMethodController<T, ID extends Serializable> extends BulkMethodController<T, ID, DeleteRequest<T, ID>> {

    public DeleteMethodController(RequestObjectFactory factory) {
        super(factory);
    }

    @Override
    public Object request(ParsedResourceControllerRequest<T> request, Resource<T, ID> resource, ResourceServiceOperations service) {
        int ids = CollectionUtils.size(request.getIds());
        if (ids == 1) {
            // delete a document
            ID id = getId(request);
            DeleteRequest<T, ID> deleteRequest = getRequest(id, request);
            return service.delete(deleteRequest);
        } else if (ids > 1) {
            BulkRequest<DeleteRequest<T, ID>> deleteRequest = getBulkRequestIds(request);
            return service.delete(deleteRequest);
        } else {
            DeleteRequest<T, ID> deleteRequest = getRequest(null, request);
            return service.deleteByQueryCriteria(deleteRequest);
        }
    }

    @Override
    DeleteRequest<T, ID> getRequest(Resource<T, ID> resource, T data, ID id, ParsedResourceControllerRequest<T> request) {
        return factory.getDeleteRequest(resource, id, request.getRequestedQueries(), request);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    BulkRequest<DeleteRequest<T, ID>> getBulkRequestIds(ParsedResourceControllerRequest<T> request) {
        Iterable<ID> iterable = (Iterable) request.getIds();
        List<DeleteRequest<T, ID>> list = new ArrayList();
        for (ID id : iterable) {
            list.add(getRequest(id, request));
        }
        return new BulkRequest<DeleteRequest<T, ID>>(list);
    }

}
