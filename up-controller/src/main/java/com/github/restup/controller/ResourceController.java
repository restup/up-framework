package com.github.restup.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.restup.controller.content.negotiation.ContentNegotiator;
import com.github.restup.controller.content.negotiation.ContentTypeNegotiation;
import com.github.restup.controller.interceptor.RequestInterceptor;
import com.github.restup.controller.linking.LinkBuilderFactory;
import com.github.restup.controller.linking.discovery.ServiceDiscovery;
import com.github.restup.controller.method.DeleteMethodController;
import com.github.restup.controller.method.GetMethodController;
import com.github.restup.controller.method.MethodController;
import com.github.restup.controller.method.PatchMethodController;
import com.github.restup.controller.method.PostMethodController;
import com.github.restup.controller.method.PutMethodController;
import com.github.restup.controller.model.HttpHeader;
import com.github.restup.controller.model.HttpMethod;
import com.github.restup.controller.model.MediaType;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerResponse;
import com.github.restup.controller.request.parser.RequestParamParser;
import com.github.restup.controller.request.parser.RequestParser;
import com.github.restup.controller.settings.ControllerSettings;
import com.github.restup.errors.StatusCode;
import com.github.restup.errors.RequestErrorException;
import com.github.restup.errors.RequestError;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.query.criteria.ResourcePathFilter.Operator;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.registry.settings.ControllerMethodAccess;
import com.github.restup.service.model.request.RequestObjectFactory;
import com.github.restup.util.Assert;
import com.google.gson.Gson;

/**
 * <ol> <li>Create A document <p>
 * <pre>
 * POST /foo
 * { data : {} }
 * </pre>
 * <p> <li>Create Multiple documents by passing an array. <p>
 * <pre>
 * POST /foo
 * { data : [{}] }
 * </pre>
 * <p> <li>Update a document by id <p>
 * <pre>
 * PUT /foo/1 { data : {} }
 * </pre>
 * <p> <li>Update multiple documents passing an array <p>
 * <pre>
 * PUT /foo { data : [{}] }
 * </pre>
 * <p> <li>Get a single document by id <p>
 * <pre>
 * GET / foo / 1
 * </pre>
 * <p> <li>List documents <p>
 * <pre>
 * GET / foo
 * </pre>
 * <p> <li>Get multiple documents by id <p>
 * <pre>
 * GET /foo/1,2,3
 * </pre>
 * <p> <li>Delete a document by id <p>
 * <pre>
 * DELETE / foo / 1
 * </pre>
 * <p> <li>Delete multiple documents by id <p>
 * <pre>
 * DELETE /foo/1,2,3
 * </pre>
 * <p> <li>Delete multiple documents matching filter criteria <p>
 * <pre>
 * DELETE /foo?filter[x]=y
 * </pre>
 * <p> <li>Patch a document by id <p>
 * <pre>
 * PATCH /foo/1 { data : {} }
 * </pre>
 * <p> <li>Patch multiple documents by id <p>
 * <pre>
 * PATCH /foo/1,2,3
 * { data : {} }
 * </pre>
 * <p> <li>Patch multiple documents matching filter criteria <p>
 * <pre>
 * PATCH /foo?filter[x]=y
 * { data : {} }
 * </pre>
 * <p> <li>Patch multiple documents passing an array <p>
 * <pre>
 * PATCH /foo { data : [{}] }
 * </pre>
 * <p> </ol>
 *
 * @author abuttaro
 */
public class ResourceController {

    private final static Logger log = LoggerFactory.getLogger(ResourceController.class);
    private final ResourceRegistry registry;
    private final ContentNegotiator[] contentNegotiators;
    private final RequestParser requestParser;
    private final RequestInterceptor interceptor;
    private final ExceptionHandler exceptionHandler;
    private final GetMethodController<?, ?> getController;
    private final DeleteMethodController<?, ?> deleteController;
    private final PatchMethodController<?, ?> patchController;
    private final PostMethodController<?, ?> postController;
    private final PutMethodController<?, ?> putController;
    public ResourceController(ControllerSettings.Builder settings) {
        this(settings.build());
    }

    @SuppressWarnings("rawtypes")
    public ResourceController(ControllerSettings settings) {
        super();
        Assert.notNull(settings, "settings are required");

        this.registry = settings.getRegistry();
        this.contentNegotiators = settings.getContentNegotiators();
        this.requestParser = settings.getRequestParser();
        this.interceptor = settings.getRequestInterceptor();
        this.exceptionHandler = settings.getExceptionHandler();

        RequestObjectFactory factory = getFactory(registry);
        getController = new GetMethodController(factory);
        deleteController = new DeleteMethodController(factory);
        patchController = new PatchMethodController(factory);
        postController = new PostMethodController(factory);
        putController = new PutMethodController(factory);
    }

    public static Builder builder() {
        return new Builder();
    }

    private static RequestObjectFactory getFactory(ResourceRegistry registry) {
        return registry.getSettings().getRequestObjectFactory();
    }

    private static <T> void validateData(ParsedResourceControllerRequest<T> parsedRequest,
            ResourceControllerRequest request, ControllerMethodAccess access, boolean itemOperation, int ids) {
        final HttpMethod method = request.getMethod();
        final int items = count(parsedRequest.getData());
        if (items == 0) {
            if (method.requiresData()) {
                throw error(request, "DATA_REQUIRED", "Document is required");
            }
        } else {
            if (!method.requiresData()) {
                throw error(request, "DATA_NOT_SUPPORTED", "Document is not supported");
            } else {
                if (items == 1) {
                    if (!itemOperation && !method.supportsCollectionOperation(access)) {
                        throw collectionResourceNotAllowed(request, access);
                    }
                } else if (items > 1) {
                    if (itemOperation || !method.supportsMultiple(access)
                            // array not supported in combination with multiple ids
                            || ids > 1) {
                        throw error(request, "DOCUMENT_ARRAY_NOT_SUPPORTED", "Array of documents not supported");
                    }
                }
            }
        }
    }

    private static int count(Object data) {
        if (data instanceof Collection) {
            return CollectionUtils.size(data);
        }
        return data == null ? 0 : 1;
    }

    private static void validateIds(ResourceControllerRequest request, ControllerMethodAccess access, int ids) {
        final HttpMethod method = request.getMethod();
        if (ids == 1) {
            if (!method.supportsItemOperation(access)) {
                throw itemResourceNotAllowed(request, access);
            }
        } else if (ids > 1) {
            if (!method.supportsAccessByIds(access)) {
                // if it doesn't support access by id then it is malformed
                if (!method.supportsItemOperation(access)) {
                    throw itemResourceNotAllowed(request, access);
                } else {
                    throw error(request, "MULTIPLE_IDS_NOT_SUPPORTED", "Multiple ids not supported");
                }
            } else if (request.getRelationship() != null) {
                throw error(request, "RELATIONSHIP_IDS_NOT_SUPPORTED",
                        "Multiple ids not supported when requesting a relationship");
            }
        } else if (ids == 0) {
            if (request.getRelationship() != null) {
                throw error(request, "RELATIONSHIP_ID_REQUIRED", "ID is required when requesting a relationship");
            }
        }
    }

    private static RequestErrorException error(ResourceControllerRequest request, String code, String detail) {
        return error(request).code(code).title("Not supported").detail(detail).buildException();
    }

    private static RequestError.Builder error(ResourceControllerRequest request) {
        return RequestError.builder().resource(request.getResource());
    }

    private static RequestErrorException itemResourceNotAllowed(ResourceControllerRequest request,
            ControllerMethodAccess access) {
        List<HttpMethod> supported = new ArrayList<HttpMethod>(HttpMethod.values().length - 1);
        for (HttpMethod m : HttpMethod.values()) {
            if (m.supportsItemOperation(access)) {
                supported.add(m);
            }
        }
        return methodNotAllowed(request, access, supported);
    }

    private static RequestErrorException collectionResourceNotAllowed(ResourceControllerRequest request,
            ControllerMethodAccess access) {
        List<HttpMethod> supported = new ArrayList<HttpMethod>(HttpMethod.values().length - 1);
        for (HttpMethod m : HttpMethod.values()) {
            if (m.supportsCollectionOperation(access)) {
                supported.add(m);
            }
        }
        return methodNotAllowed(request, access, supported);
    }

    private static RequestErrorException methodNotAllowed(ResourceControllerRequest request,
            ControllerMethodAccess access, List<HttpMethod> supported) {
        return error(request).status(StatusCode.METHOD_NOT_ALLOWED)
                .code(StatusCode.METHOD_NOT_ALLOWED.name()).meta(HttpHeader.Allow.name(), supported)
                // TODO The response MUST query an Allow header containing a list of valid
                // methods for the requested resource.
                .buildException();
    }

    private static ControllerMethodAccess getMethodAccess(ResourceRegistry registry, Resource<?, ?> resource) {
        if (resource != null && resource.getControllerAccess() != null) {
            return resource.getControllerAccess();
        }
        return registry.getSettings().getDefaultControllerAccess();
    }

    @SuppressWarnings({"unused", "rawtypes"})
    private static void addUpdateFields(ParsedResourceControllerRequest.Builder<?> builder,
            List<MappedField<?>> fields) {
        if (builder.getData() instanceof Collection) {
            int i = 0;
            for (Object item : (Collection) builder.getData()) {
                // add the field for each item in the request
                addUpdateFields(builder, fields, i++);
            }
        } else {
            addUpdateFields(builder, fields, null);
        }
    }

    private static void addUpdateFields(ParsedResourceControllerRequest.Builder<?> builder, List<MappedField<?>> fields,
            Integer i) {
    		fields.stream()
    			.filter(f -> !f.isImmutable())
    			.map(f -> builder.addRequestedPath(i, f));
    }

    public Object handleException(ResourceControllerRequest request, ResourceControllerResponse response, Throwable e) {
        return exceptionHandler.handleException(request, response, e);
    }

    /**
     * Handles exceptions when building {@link ResourceControllerRequest} and handling request
     */
    public <T, ID extends Serializable> Object request(ResourceControllerRequest.AbstractBuilder<?, ?> builder,
            ResourceControllerResponse response) {
        ResourceControllerRequest request = null;
        try {
            request = builder.build();
            return requestInternal(request, response);
        } catch (Throwable e) {
            return handleException(request, response, e);
        }
    }

    public <T, ID extends Serializable> Object request(ResourceControllerRequest request,
            ResourceControllerResponse response) {
        try {
            return requestInternal(request, response);
        } catch (Throwable e) {
            return handleException(request, response, e);
        }
    }

    @SuppressWarnings("unchecked")
    <T, ID extends Serializable> Object requestInternal(ResourceControllerRequest request,
            ResourceControllerResponse response) {
        Assert.notNull(request, "request is required");
        Assert.notNull(response, "response is required");
        Assert.notNull(request.getMethod(), "method is required");
        Assert.notNull(request.getResource(), "resource is required");

        final ControllerMethodAccess access = getMethodAccess(registry, request.getResource());
        final int ids = CollectionUtils.size(request.getIds());
        boolean itemOperation = ids == 1;

        // confirm content type is supported ahead of parsing work
        ContentNegotiator contentNegotiator = getContentNegotiator(request);

        MethodController<T, ID> methodController = getMethodController(request, access, itemOperation);

        validateIds(request, access, ids);

        // parse the request
        ParsedResourceControllerRequest<T> parsedRequest = parseRequest(request);

        // validate data passed in the message body
        validateData(parsedRequest, request, access, itemOperation, ids);

        interceptor.before(parsedRequest);
        Object result = methodController.request(parsedRequest);
        interceptor.after(parsedRequest);

        response.setStatus(methodController.getSuccessStatus());
        return contentNegotiator.formatResponse(parsedRequest, response, result);
    }

    /**
     * @return ContentNegotiator for content type passed
     * @throws RequestErrorException if content type is not supported
     */
    private ContentNegotiator getContentNegotiator(ResourceControllerRequest request) {
        for (ContentNegotiator contentNegotiator : contentNegotiators) {
            if (contentNegotiator.accept(request)) {
                return contentNegotiator;
            }
        }
        throw RequestError.builder(request.getResource())
                .status(StatusCode.UNSUPPORTED_MEDIA_TYPE)
                .meta(ContentTypeNegotiation.CONTENT_TYPE, request.getContentType())
                .buildException();
    }

    @SuppressWarnings({"rawtypes"})
    private MethodController getMethodController(ResourceControllerRequest request, ControllerMethodAccess access,
            boolean itemOperation) {
        switch (request.getMethod()) {
            case GET:
                return getController;
            case PATCH:
                return patchController;
            case POST:
                return postController;
            case PUT:
                return putController;
            case DELETE:
                return deleteController;
        }
        if (itemOperation) {
            throw itemResourceNotAllowed(request, access);
        } else {
            throw collectionResourceNotAllowed(request, access);
        }
    }

    private <T> ParsedResourceControllerRequest<T> parseRequest(ResourceControllerRequest request) {
        ParsedResourceControllerRequest.Builder<T> builder = new ParsedResourceControllerRequest.Builder<T>(registry,
                request);
        requestParser.parse(request, builder);
        if (CollectionUtils.size(request.getIds()) > 1) {
            // Add filter for ids
            MappedField<?> field = request.getResource().getIdentityField();
            builder.addFilter("pathIds", request.getIds(), field.getApiName(), Operator.in, request.getIds());
        }
        if (HttpMethod.PUT == request.getMethod()) {
            // PUT is effectively PATCH all so we have to update all fields
            List<MappedField<?>> fields = request.getResource().getMapping().getAttributes();
            addUpdateFields(builder, fields);
        }
        return builder.build();
    }

    public static class Builder {

        ControllerSettings.Builder settings;

        public Builder() {
            settings = ControllerSettings.builder();
        }

        private Builder me() {
            return this;
        }

        public Builder registry(ResourceRegistry registry) {
            settings.registry(registry);
            return me();
        }

        public Builder contentNegotiators(ContentNegotiator... contentNegotiator) {
            settings.contentNegotiators(contentNegotiator);
            return me();
        }

        public Builder interceptors(RequestInterceptor... interceptors) {
            settings.interceptors(interceptors);
            return me();
        }

        public Builder requestParsers(RequestParser... requestParsers) {
            settings.requestParsers(requestParsers);
            return me();
        }

        public Builder requestParamParsers(RequestParamParser... requestParamParsers) {
            settings.requestParamParsers(requestParamParsers);
            return me();
        }

        public Builder exceptionHandler(ExceptionHandler exceptionHandler) {
            settings.exceptionHandler(exceptionHandler);
            return me();
        }

        public Builder relationshipParser(RequestParser relationshipParser) {
            settings.relationshipParser(relationshipParser);
            return me();
        }

        public Builder linkBuilderFactory(LinkBuilderFactory linkBuilderFactory) {
            settings.linkBuilderFactory(linkBuilderFactory);
            return me();
        }

        public Builder serviceDiscovery(ServiceDiscovery serviceDiscovery) {
            settings.serviceDiscovery(serviceDiscovery);
            return me();
        }

        public Builder jacksonObjectMapper(ObjectMapper mapper) {
            settings.jacksonObjectMapper(mapper);
            return me();
        }

        public Builder gson(Gson gson) {
            settings.gson(gson);
            return me();
        }

        public Builder autoDetectDisabled(boolean b) {
            settings.autoDetectDisabled(b);
            return me();
        }

        /**
         * Use the content negotiator for the specified mediaType as the default content negotiator. This will allow the mediaType to be rendered in a browser by default if desired.
         *
         * @throws IllegalArgumentException if the specified mediaType is not supported
         */
        public Builder defaultMediaType(String mediaType) {
            settings.defaultMediaType(mediaType);
            return me();
        }

        /**
         * @see #defaultMediaType(String)
         */
        public Builder defaultMediaType(MediaType mediaType) {
            return defaultMediaType(mediaType.getContentType());
        }

        public ResourceController build() {
            return new ResourceController(settings);
        }

    }

}
