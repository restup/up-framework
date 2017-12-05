package com.github.restup.controller.method;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.registry.Resource;
import com.github.restup.service.ResourceServiceOperations;
import com.github.restup.service.model.request.BulkRequest;
import com.github.restup.service.model.request.RequestObjectFactory;
import com.github.restup.service.model.request.UpdateRequest;
import java.io.Serializable;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Handle PUT operations <ul> <li>Multiple documents</li> <li>A single document</li> <li>Multiple document matching query criteria</li> </ul>
 */
public class PatchMethodController<T, ID extends Serializable> extends BulkMethodController<T, ID, UpdateRequest<T, ID>> {

    public PatchMethodController(RequestObjectFactory factory) {
        super(factory);
    }

    public Object request(ParsedResourceControllerRequest<T> request, Resource<T, ID> resource, ResourceServiceOperations service) {
        int ids = CollectionUtils.size(request.getIds());
        if (ids == 1) {
            ID id = getId(request);
            UpdateRequest<T, ID> updateRequest = getRequest(id, request);
            return service.update(updateRequest);
        }
        if (isBulk(request)) {
            BulkRequest<UpdateRequest<T, ID>> updateRequest = getIdentifiedBulkRequest(resource, request);
            return service.update(updateRequest);
//		} else if ( ids > 1 ) {
            //XXX patch by ids will behave as a update by query criteria.
            // perhaps it should have its own service method to allow 1:1 configuration
            // of service/controller access settings
        } else {
            UpdateRequest<T, ID> updateRequest = getRequest(null, request);
            return service.updateByQueryCriteria(updateRequest);
        }
    }

    @Override
    UpdateRequest<T, ID> getRequest(Resource<T, ID> resource, T data, ID id, ParsedResourceControllerRequest<T> request) {
        return factory.getUpdateRequest(resource, id, request.getData(), request.getRequestedPaths(), request.getRequestedQueries(), request);
    }

}
