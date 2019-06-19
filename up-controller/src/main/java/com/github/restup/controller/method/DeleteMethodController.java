package com.github.restup.controller.method;

import com.github.restup.annotations.model.DeleteStrategy;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerResponse;
import com.github.restup.registry.Resource;
import com.github.restup.response.strategy.DeleteStrategySupplier;
import com.github.restup.service.ResourceServiceOperations;
import com.github.restup.service.model.request.BulkRequest;
import com.github.restup.service.model.request.DeleteRequest;
import com.github.restup.service.model.request.RequestObjectFactory;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

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
public class DeleteMethodController<T, ID extends Serializable> extends
    BulkMethodController<T, ID, DeleteRequest<T, ID>, DeleteStrategySupplier> {

    public DeleteMethodController(RequestObjectFactory factory) {
        super(factory);
    }

    @Override
    public Object request(ParsedResourceControllerRequest<T> request,
        ResourceControllerResponse response, Resource<T, ID> resource,
        ResourceServiceOperations service) {
        int ids = CollectionUtils.size(request.getIds());

        DeleteStrategySupplier strategy = resource.getRegistry().getSettings()
            .getDeleteStrategySupplier();
        DeleteStrategy strategyType;
        Object result;
        if (ids == 1) {
            // delete a document
            DeleteRequest<T, ID> deleteRequest = getRequestByPathId(request, strategy);
            strategyType = deleteRequest.getDeleteStrategy();
            result = service.delete(deleteRequest);
        } else if (ids > 1) {
            BulkRequest<DeleteRequest<T, ID>> deleteRequest = getBulkRequestIds(request, strategy);
            strategyType = strategy.getStrategy(resource);
            result = service.delete(deleteRequest);
        } else {
            DeleteRequest<T, ID> deleteRequest = getRequest(request, strategy);
            strategyType = deleteRequest.getDeleteStrategy();
            result = service.deleteByQueryCriteria(deleteRequest);
        }
        status(request, response, result, strategyType);
        return result;
    }

    @Override
    DeleteRequest<T, ID> getRequest(Resource<T, ID> resource, T data, ID id,
        ParsedResourceControllerRequest<T> request, DeleteStrategySupplier strategy) {
        DeleteStrategy strategyType = strategy.getStrategy(resource);
        return factory
            .getDeleteRequest(resource, id, request.getRequestedQueries(), request, strategyType);
    }

    BulkRequest<DeleteRequest<T, ID>> getBulkRequestIds(ParsedResourceControllerRequest<T> request,
        DeleteStrategySupplier strategy) {
        Iterable<ID> iterable = (Iterable) request.getIds();
        List<DeleteRequest<T, ID>> list = new ArrayList();
        for (ID id : iterable) {
            list.add(getRequest(id, request, strategy));
        }
        return new BulkRequest<>(list);
    }

}
