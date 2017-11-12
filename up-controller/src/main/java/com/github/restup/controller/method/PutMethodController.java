package com.github.restup.controller.method;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.registry.Resource;
import com.github.restup.service.ResourceService;
import com.github.restup.service.model.request.BulkRequest;
import com.github.restup.service.model.request.RequestObjectFactory;
import com.github.restup.service.model.request.UpdateRequest;

import java.io.Serializable;

/**
 * Handle PUT operations
 * <ul>
 * <li>Multiple documents</li>
 * <li>A single document</li>
 * </ul>
 *
 * @param <T>
 * @param <ID>
 */
public class PutMethodController<T, ID extends Serializable> extends BulkMethodController<T, ID, UpdateRequest<T, ID>> {

    public PutMethodController(RequestObjectFactory factory) {
        super(factory);
    }

    public Object request(ParsedResourceControllerRequest<T> request, Resource<T, ID> resource, ResourceService<T, ID> service) {
        if (isBulk(request)) {
            // update multiple documents
            BulkRequest<UpdateRequest<T, ID>> updateRequest = getIdentifiedBulkRequest(resource, request);
            return service.update(updateRequest);
        } else {
            // update a single document
            ID id = getId(request);
            UpdateRequest<T, ID> updateRequest = getRequest(id, request);
            return service.update(updateRequest);
        }
    }

    @Override
    UpdateRequest<T, ID> getRequest(Resource<T, ID> resource, T data, ID id, ParsedResourceControllerRequest<T> request) {
        return factory.getUpdateRequest(resource, id, request.getData(), request.getRequestedPaths(), request.getRequestedQueries(), request);
    }

}
