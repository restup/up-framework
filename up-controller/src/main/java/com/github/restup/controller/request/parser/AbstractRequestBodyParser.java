package com.github.restup.controller.request.parser;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.errors.ErrorCode;
import com.github.restup.path.ResourcePath;
import com.github.restup.registry.Resource;
import com.github.restup.registry.settings.ControllerMethodAccess;
import com.github.restup.service.model.ResourceData;

/**
 * Abstract implementation for handling request body, providing commons support for detecting error
 * conditions.
 *
 * @param <T> type of json object
 */
public abstract class AbstractRequestBodyParser<T> implements RequestParser {

    protected static ResourcePath.Builder path(ResourcePath parent,
        ParsedResourceControllerRequest.Builder<?> builder, String fieldName) {
        return path(parent).setErrors(builder).append(fieldName);
    }

    protected static ResourcePath.Builder path(ResourcePath parent) {
        return ResourcePath.builder(parent).setMode(ResourcePath.Builder.Mode.API).setQuiet(true);
    }

    @Override
    public void parse(ResourceControllerRequest request, ParsedResourceControllerRequest.Builder builder) {
        Resource resource = request.getResource();
        T body = this.getBody(request);
        if (body == null) {
            if (request.getMethod().requiresData()) {
                // TODO bind params to new instance when method param is present?
                builder.addError(ErrorCode.BODY_REQUIRED);
            }
            return;
        } else if (this.isArray(body)) {
            ControllerMethodAccess controllerMethodAccess = resource.getControllerMethodAccess();
            if (!request.getMethod().supportsMultiple(controllerMethodAccess)) {
                builder.addError(ErrorCode.BODY_ARRAY_NOT_SUPPORTED);
                return;
            }
        }
        ResourcePath path = ResourcePath.builder(resource).data().build();
        this.graph(request, builder, resource, path, body);

        if (!builder.hasErrors()) {
            Object deserialized = this.deserializeBody(request, builder, body);
        		deserialized = Resource.validate(resource, deserialized);
            builder.setData(deserialized);
        }
    }

    /**
     * @param t object to check
     * @return true if t is an array
     */
    abstract protected boolean isArray(T t);

    /**
     * @return true if t is an o
     * @param t object to checkbject
     */
    abstract protected boolean isObject(T t);

    /*TODO
     * Deserialize an array of documents.  should iterate and call {@link #deserializeObject(ResourceControllerRequest, ParsedResourceControllerRequest.Builder, Object)}
     *
     * @param request
     * @param builder
     * @param body
     * @return
     */
    abstract protected Object deserializeArray(ResourceControllerRequest request, ParsedResourceControllerRequest.Builder<?> builder, T body);

    /*TODO
     * Deserialize the document to the correct type for the resource requested
     *
     * @param request
     * @param builder
     * @param body
     * @return
     */
    abstract protected Object deserializeObject(ResourceControllerRequest request, ParsedResourceControllerRequest.Builder<?> builder, T body);

    /*TODO
     * Iterate over the array and {@link #graphObject(ResourceControllerRequest, ParsedResourceControllerRequest.Builder, Resource, ResourcePath, Object)}
     *
     * @param request
     * @param builder
     * @param resource
     * @param parent
     * @param node
     */
    abstract protected void graphArray(ResourceControllerRequest request, ParsedResourceControllerRequest.Builder<?> builder, Resource<?, ?> resource, ResourcePath parent, T node);

    /*TODO
     * Implementation should iterate over object fields, building a path for each. At each path the {@link #graph(ResourceControllerRequest, ParsedResourceControllerRequest.Builder, Resource, ResourcePath, Object)} should be called with the resulting {@link ResourcePath} and value of the field.
     *
     * @param request
     * @param builder
     * @param resource
     * @param parent
     * @param node
     */
    abstract protected void graphObject(ResourceControllerRequest request, ParsedResourceControllerRequest.Builder<?> builder, Resource<?, ?> resource, ResourcePath parent, T node);

    protected Object deserializeBody(ResourceControllerRequest request, ParsedResourceControllerRequest.Builder<?> builder, T body) {
        if (this.isArray(body)) {
            return this.deserializeArray(request, builder, body);
        } else {
            return this.deserializeObject(request, builder, body);
        }
    }

    protected void graph(ResourceControllerRequest request, ParsedResourceControllerRequest.Builder<?> builder, Resource<?, ?> resource, ResourcePath parent, T node) {
        if (parent.isValid()) {
            if (this.isArray(node)) {
                this.graphArray(request, builder, resource, parent, node);
//            } else if (expectsArray(parent)) {
                //TODO check array required, error
            } else if (this.isObject(node)) {
                //TODO if is polymorphic type, type identifier is required.
                //TODO if patch lookup existing type identifier
                this.graphObject(request, builder, resource, parent, node);
            } else {
                builder.addRequestedPath(parent);
            }
        } else {
            builder.addRequestedPath(parent);
        }
    }

    private T getBody(ResourceControllerRequest request) {
        ResourceData<?> body = request.getBody();
        return body == null ? null : (T) body.getData();
    }

}
