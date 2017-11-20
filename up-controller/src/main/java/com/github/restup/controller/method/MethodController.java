package com.github.restup.controller.method;

import com.github.restup.controller.ResourceController;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.registry.Resource;
import com.github.restup.service.ResourceServiceOperations;
import com.github.restup.service.model.request.RequestObjectFactory;

import java.io.Serializable;

/**
 * A controller to handle an individual method to reduce complexity of {@link ResourceController} routing.
 *
 * @author abuttaro
 */
public abstract class MethodController<T, ID extends Serializable> {

    final RequestObjectFactory factory;

    public MethodController(RequestObjectFactory factory) {
        super();
        this.factory = factory;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public final Object request(ParsedResourceControllerRequest<T> request) {
        Resource<T, ID> resource = (Resource) request.getResource();
        ResourceServiceOperations service = resource.getServiceOperations();
        return request(request, resource, service);
    }

    abstract Object request(ParsedResourceControllerRequest<T> request, Resource<T, ID> resource, ResourceServiceOperations service);

    @SuppressWarnings("unchecked")
    ID getId(ParsedResourceControllerRequest<T> request) {
        return (ID) request.getIds().iterator().next();
    }

    public int getSuccessStatus() {
        return 200;
    }
}
