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
abstract class BulkMethodController<T, ID extends Serializable, R> extends MethodController<T, ID> {

    public BulkMethodController(RequestObjectFactory factory) {
        super(factory);
    }

    abstract R getRequest(Resource<T, ID> resource, T data, ID id, ParsedResourceControllerRequest<T> request);

    boolean isBulk(ParsedResourceControllerRequest<?> request) {
        return request.getData() instanceof Collection
                && CollectionUtils.size(request.getData()) > 1;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    BulkRequest<R> getIdentifiedBulkRequest(Resource<T, ID> resource, ParsedResourceControllerRequest<T> request) {
        Iterable<T> iterable = (Iterable) request.getData();
        List list = new ArrayList();
        ResourcePath idPath = ResourcePath.idPath(resource);
        for (T data : iterable) {
            Object id = idPath.getValue(request.getData());
            list.add(getRequest(resource, data, (ID) id, request));
        }
        return new BulkRequest(list);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    BulkRequest<R> getBulkRequest(ParsedResourceControllerRequest<T> request) {
        Iterable<T> iterable = (Iterable) request.getData();
        List list = new ArrayList<R>();
        for (T data : iterable) {
            list.add(getRequest((Resource) request.getResource(), data, null, request));
        }
        return new BulkRequest(list);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    R getRequest(ID id, ParsedResourceControllerRequest<T> request) {
        Resource<T, ID> resource = (Resource) request.getResource();
        return getRequest(resource, request.getData(), id, request);
    }
}
