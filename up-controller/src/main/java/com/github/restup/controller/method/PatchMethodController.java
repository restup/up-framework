package com.github.restup.controller.method;

import com.github.restup.annotations.model.UpdateStrategy;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerResponse;
import com.github.restup.registry.Resource;
import com.github.restup.response.strategy.UpdateStrategySupplier;
import com.github.restup.service.ResourceServiceOperations;
import com.github.restup.service.model.request.BulkRequest;
import com.github.restup.service.model.request.RequestObjectFactory;
import com.github.restup.service.model.request.UpdateRequest;
import java.io.Serializable;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Handle PUT operations
 * <ul>
 * <li>Multiple documents</li>
 * <li>A single document</li>
 * <li>Multiple document matching query criteria</li>
 * </ul>
 * 
 * @param <T> resource type
 * @param <ID> resource id type
 */
public class PatchMethodController<T, ID extends Serializable> extends
    BulkMethodController<T, ID, UpdateRequest<T, ID>, UpdateStrategySupplier> {

    public PatchMethodController(RequestObjectFactory factory) {
        super(factory);
    }

    @Override
    public Object request(ParsedResourceControllerRequest<T> request,
        ResourceControllerResponse response, Resource<T, ID> resource,
        ResourceServiceOperations service) {
        int ids = CollectionUtils.size(request.getIds());

        UpdateStrategySupplier strategy = resource.getRegistry().getSettings()
            .getUpdateStrategySupplier();
        UpdateStrategy strategyType;

        Object result;
        if (ids == 1) {
            UpdateRequest<T, ID> updateRequest = getRequestByPathId(request, strategy);
            strategyType = updateRequest.getUpdateStrategy();
            result = service.update(updateRequest);
        } else if (isBulk(request)) {
            strategyType = strategy.getStrategy(resource);
            BulkRequest<UpdateRequest<T, ID>> updateRequest = getBulkRequest(resource, request,
                strategy);
            result = service.update(updateRequest);
//		} else if ( ids > 1 ) {
            //XXX patch by ids will behave as a update by query criteria.
            // perhaps it should have its own service method to allow 1:1 configuration
            // of service/controller access settings
        } else {
            UpdateRequest<T, ID> updateRequest = getRequest(null, request, strategy);
            strategyType = updateRequest.getUpdateStrategy();
            result = service.updateByQueryCriteria(updateRequest);
        }
        status(request, response, result, strategyType);
        return result;
    }

    @Override
    UpdateRequest<T, ID> getRequest(Resource<T, ID> resource, T data, ID id,
        ParsedResourceControllerRequest<T> request, UpdateStrategySupplier strategy) {

        UpdateStrategy strategyType = strategy.getStrategy(resource);
        return factory
            .getUpdateRequest(resource, id, request.getData(), request.getRequestedPaths(),
                request.getRequestedQueries(), request, strategyType);
    }

}
