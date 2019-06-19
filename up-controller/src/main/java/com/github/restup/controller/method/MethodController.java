package com.github.restup.controller.method;

import static com.github.restup.util.UpUtils.nvl;

import com.github.restup.annotations.model.StatusCode;
import com.github.restup.annotations.model.StatusCodeProvider;
import com.github.restup.controller.ResourceController;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerResponse;
import com.github.restup.registry.Resource;
import com.github.restup.service.ResourceServiceOperations;
import com.github.restup.service.model.request.RequestObjectFactory;
import java.io.Serializable;

/**
 * A controller to handle an individual method to reduce complexity of {@link ResourceController}
 * routing.
 *
 * @param <T> resource type
 * @param <ID> resource id type
 * @author abuttaro
 */
public abstract class MethodController<T, ID extends Serializable> {

    final RequestObjectFactory factory;

    public MethodController(RequestObjectFactory factory) {
        super();
        this.factory = factory;
    }

    static void status(ParsedResourceControllerRequest<?> request,
        ResourceControllerResponse response, Object result, StatusCodeProvider status) {

        StatusCode code = null;
        if (result instanceof StatusCodeProvider && request.isJsonApi()) {
            code = ((StatusCodeProvider) result).getStatusCode();
        }

        code = nvl(code, () -> status.getStatusCode());
        code = nvl(code, StatusCode.OK);
        response.setStatus(code);
    }

    public final Object request(ParsedResourceControllerRequest<T> request,
        ResourceControllerResponse response) {
        Resource<T, ID> resource = (Resource) request.getResource();
        ResourceServiceOperations service = resource.getServiceOperations();
        Object result = request(request, response, resource, service);
        return result;
    }

    abstract Object request(ParsedResourceControllerRequest<T> request,
        ResourceControllerResponse response, Resource<T, ID> resource,
        ResourceServiceOperations service);

    ID getId(ParsedResourceControllerRequest<T> request) {
        return (ID) request.getIds().iterator().next();
    }

}
