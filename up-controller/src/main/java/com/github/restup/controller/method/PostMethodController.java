package com.github.restup.controller.method;

import java.io.Serializable;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.registry.Resource;
import com.github.restup.service.ResourceServiceOperations;
import com.github.restup.service.model.request.BulkRequest;
import com.github.restup.service.model.request.CreateRequest;
import com.github.restup.service.model.request.RequestObjectFactory;

/**
 * Handle POST operations
 * <ul>
 * <li>Multiple documents</li>
 * <li>A single document</li>
 * </ul>
 * 
 * @param <T> resource type
 * @param <ID> resource id type
 */
public class PostMethodController<T, ID extends Serializable> extends BulkMethodController<T, ID, CreateRequest<T>> {

    public PostMethodController(RequestObjectFactory factory) {
        super(factory);
    }

    @Override
    public Object request(ParsedResourceControllerRequest<T> request, Resource<T, ID> resource, ResourceServiceOperations service) {
        if (isBulk(request)) {
            // create multiple documents
            BulkRequest<CreateRequest<T>> createRequest = getBulkRequest(request);
            return service.create(createRequest);
        } else {
            // create a single document
            CreateRequest<T> createRequest = getRequest(null, request);
            return service.create(createRequest);
        }
    }

    @Override
    CreateRequest<T> getRequest(Resource<T, ID> resource, T data, ID id, ParsedResourceControllerRequest<T> request) {
        return factory.getCreateRequest(resource, request.getData(), request.getRequestedPaths(), request.getRequestedQueries(), request);
    }

    @Override
    public int getSuccessStatus() {
        return 201;
    }

}
