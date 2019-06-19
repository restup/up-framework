package com.github.restup.controller.method;

import static com.github.restup.util.UpUtils.nvl;

import com.github.restup.annotations.model.CreateStrategy;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerResponse;
import com.github.restup.registry.Resource;
import com.github.restup.response.strategy.CreateStrategySupplier;
import com.github.restup.service.ResourceServiceOperations;
import com.github.restup.service.model.request.BulkRequest;
import com.github.restup.service.model.request.CreateRequest;
import com.github.restup.service.model.request.RequestObjectFactory;
import java.io.Serializable;

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
public class PostMethodController<T, ID extends Serializable> extends
    BulkMethodController<T, ID, CreateRequest<T>, CreateStrategySupplier> {

    public PostMethodController(RequestObjectFactory factory) {
        super(factory);
    }

    @Override
    public Object request(ParsedResourceControllerRequest<T> request,
        ResourceControllerResponse response, Resource<T, ID> resource,
        ResourceServiceOperations service) {

        CreateStrategySupplier strategy = resource.getRegistry().getSettings()
            .getCreateStrategySupplier();
        CreateStrategy strategyType;

        Object result;
        if (isBulk(request)) {
            // create multiple documents
            strategyType = strategy.getStrategy(resource, null);
            BulkRequest<CreateRequest<T>> createRequest = getBulkRequest(resource, request,
                strategy);
            result = service.create(createRequest);
        } else {
            // use id from body if present for client generated ids
            // validations will reject if not supported
            CreateRequest<T> createRequest = getRequestByBodyId(request, strategy);
            strategyType = createRequest.getCreateStrategy();
            result = service.create(createRequest);
            //TODO Location Header
        }
        status(request, response, result, strategyType);
        return result;
    }

    @Override
    CreateRequest<T> getRequest(Resource<T, ID> resource, T data, ID identity,
        ParsedResourceControllerRequest<T> request, CreateStrategySupplier strategy) {
        ID id = nvl(identity, () -> resource.getIdentityField().readValue(data));
        CreateStrategy type = strategy.getStrategy(resource, id);

        return factory.getCreateRequest(resource, request.getData(), request.getRequestedPaths(),
            request.getRequestedQueries(), request, type);
    }

}
