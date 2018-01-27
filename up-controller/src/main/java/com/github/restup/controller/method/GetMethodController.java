package com.github.restup.controller.method;

import static com.github.restup.util.UpUtils.getFirst;
import java.io.Serializable;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRelationship;
import com.github.restup.service.ResourceServiceOperations;
import com.github.restup.service.model.request.ListRequest;
import com.github.restup.service.model.request.ReadRequest;
import com.github.restup.service.model.request.RequestObjectFactory;
import com.github.restup.service.model.response.ReadResult;

/**
 * Handle GET operations <ul> <li>List documents (matching optional query criteria)</li> <li>A single document</li> </ul>
 */
public class GetMethodController<T, ID extends Serializable> extends MethodController<T, ID> {

    public GetMethodController(RequestObjectFactory factory) {
        super(factory);
    }

    @Override
    public Object request(ParsedResourceControllerRequest<T> request, Resource<T, ID> resource, ResourceServiceOperations service) {
        if (request.getResourceRelationship() != null) {
            return findRelationship(request, resource);
        } else {
            int ids = CollectionUtils.size(request.getIds());
            if (ids == 1) {
                // get a single document
                ID id = getId(request);
                ReadRequest<T, ID> readRequest = factory.getReadRequest(resource, id, request.getRequestedQueries(), request);
                return service.find(readRequest);
                //		} else if (ids > 1) {
                //XXX  get by ids will behave as a list operation, using filters added by
                // ResourceController.
            } else {
                // list documents
                ListRequest<T> readRequest = factory.getListRequest(resource, request.getRequestedQueries(), request);
                return service.list(readRequest);
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Object findRelationship(ParsedResourceControllerRequest<T> request, Resource<T, ID> resource) {
        ResourceRelationship relationship = request.getResourceRelationship();
        ListRequest<T> readRequest = factory.getListRequest(resource, request.getRequestedQueries(), request);
        ReadResult<List<T>> result = resource.getService().list(readRequest);
        // if relationship is to one resource, then return an item, not collection
        if (relationship.isToOneRelationship(request.getRelationship())) {
            return ReadResult.of(getFirst(result.getData()));
        }
        return result;
    }

}
