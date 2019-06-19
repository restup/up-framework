package com.github.restup.controller.method;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.path.ResourcePath;
import com.github.restup.registry.Resource;
import com.github.restup.service.model.request.BulkRequest;
import com.github.restup.service.model.request.RequestObjectFactory;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Base implementation supporting bulk operations updating multiple documents
 */
abstract class BulkMethodController<T, ID extends Serializable, R, Strategy> extends
    MethodController<T, ID> {

    public BulkMethodController(RequestObjectFactory factory) {
        super(factory);
    }


    static boolean isBulk(ParsedResourceControllerRequest<?> request) {
        return request.getData() instanceof Collection
                && CollectionUtils.size(request.getData()) > 1;
    }

    abstract R getRequest(Resource<T, ID> resource, T data, ID id,
        ParsedResourceControllerRequest<T> request, Strategy strategy);

    BulkRequest<R> getBulkRequest(Resource<T, ID> resource,
        ParsedResourceControllerRequest<T> request, Strategy strategy) {
        Iterable<T> iterable = (Iterable) request.getData();
        List list = new ArrayList();
        ResourcePath idPath = ResourcePath.idPath(resource);
        for (T data : iterable) {
            list.add(getRequestByBodyId(request, strategy, resource, data, idPath));
        }
        return new BulkRequest(list);
    }

    R getRequestByBodyId(ParsedResourceControllerRequest<T> request,
        Strategy strategy) {
        return getRequestByBodyId(request, strategy, (Resource<T, ID>) request.getResource(),
            request.getData(),
            ResourcePath.idPath(request.getResource()));
    }

    R getRequestByBodyId(ParsedResourceControllerRequest<T> request, Strategy strategy,
        Resource<T, ID> resource, T data, ResourcePath idPath) {
        Object id = idPath.getValue(data);
        return getRequest(resource, data, (ID) id, request, strategy);
    }

    R getRequestByPathId(ParsedResourceControllerRequest<T> request, Strategy strategy) {
        ID id = getId(request);
        return getRequest(id, request, strategy);
    }

    R getRequest(ParsedResourceControllerRequest<T> request, Strategy strategy) {
        return getRequest(null, request, strategy);
    }

    R getRequest(ID id, ParsedResourceControllerRequest<T> request, Strategy strategy) {
        Resource<T, ID> resource = (Resource) request.getResource();
        return getRequest(resource, request.getData(), id, request, strategy);
    }
}
