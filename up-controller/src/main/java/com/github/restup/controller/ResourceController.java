package com.github.restup.controller;

import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.restup.annotations.model.StatusCode;
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
import com.github.restup.controller.model.AbstractResourceControllerRequestBuilder;
import com.github.restup.controller.model.HttpHeader;
import com.github.restup.controller.model.HttpMethod;
import com.github.restup.controller.model.MediaType;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerResponse;
import com.github.restup.controller.request.parser.RequestParser;
import com.github.restup.controller.request.parser.path.RequestPathParser;
import com.github.restup.controller.request.parser.path.RequestPathParserResult;
import com.github.restup.controller.settings.ControllerSettings;
import com.github.restup.errors.RequestError;
import com.github.restup.errors.RequestErrorException;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.query.criteria.ResourcePathFilter.Operator;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.registry.settings.ControllerMethodAccess;
import com.github.restup.service.model.request.RequestObjectFactory;
import com.github.restup.util.Assert;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <ol>
 * <li>Create A document
 * 
 * <pre>
 * POST /foo
 * { data : {} }
 * </pre>
 * 
 * </li>
 * <li>Create Multiple documents by passing an array.
 * 
 * <pre>
 * POST /foo
 * { data : [{}] }
 * </pre>
 * 
 * </li>
 * <li>Update a document by id
 * 
 * <pre>
 * PUT /foo/1 { data : {} }
 * </pre>
 * 
 * <li>Update multiple documents passing an array
 * 
 * <pre>
 * PUT /foo { data : [{}] }
 * </pre>
 * 
 * </li>
 * <li>Get a single document by id
 * 
 * <pre>
 * GET / foo / 1
 * </pre>
 * 
 * </li>
 * <li>List documents
 * 
 * <pre>
 * GET / foo
 * </pre>
 * 
 * </li>
 * <li>Get multiple documents by id
 * 
 * <pre>
 * GET /foo/1,2,3
 * </pre>
 * 
 * </li>
 * <li>Delete a document by id
 * 
 * <pre>
 * DELETE / foo / 1
 * </pre>
 * 
 * </li>
 * <li>Delete multiple documents by id
 * 
 * <pre>
 * DELETE /foo/1,2,3
 * </pre>
 * 
 * </li>
 * <li>Delete multiple documents matching filter criteria
 * 
 * <pre>
 * DELETE /foo?filter[x]=y
 * </pre>
 * 
 * </li>
 * <li>Patch a document by id
 * 
 * <pre>
 * PATCH /foo/1 { data : {} }
 * </pre>
 * 
 * </li>
 * <li>Patch multiple documents by id
 * 
 * <pre>
 * PATCH /foo/1,2,3
 * { data : {} }
 * </pre>
 * 
 * </li>
 * <li>Patch multiple documents matching filter criteria
 * 
 * 
 * <pre>
 * PATCH /foo?filter[x]=y
 * { data : {} }
 * </pre>
 * 
 * </li>
 * <li>Patch multiple documents passing an array
 * 
 * 
 * <pre>
 * PATCH /foo { data : [{}] }
 * </pre>
 * 
 * </li>
 * </ol>
 *
 * @author abuttaro
 */
public class ResourceController {

    private final static Logger log = LoggerFactory.getLogger(ResourceController.class);
    private final ResourceRegistry registry;
    private final ContentNegotiator contentNegotiator;
    private final RequestParser requestParser;
    private final RequestPathParser requestPathParser;
    private final RequestInterceptor interceptor;
    private final ExceptionHandler exceptionHandler;
    private final GetMethodController<?, ?> getController;
    private final DeleteMethodController<?, ?> deleteController;
    private final PatchMethodController<?, ?> patchController;
    private final PostMethodController<?, ?> postController;
    private final PutMethodController<?, ?> putController;
    private final String mediaTypeParam;

    public ResourceController(ControllerSettings.Builder settings) {
        this(settings.build());
    }

    public ResourceController(ControllerSettings settings) {
        super();
        Assert.notNull(settings, "settings are required");

        registry = settings.getRegistry();
        contentNegotiator = settings.getContentNegotiator();
        requestParser = settings.getRequestParser();
        requestPathParser = settings.getRequestPathParser();
        interceptor = settings.getRequestInterceptor();
        exceptionHandler = settings.getExceptionHandler();
        mediaTypeParam = settings.getMediaTypeParam();

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
        ResourceControllerRequest request, RequestPathParserResult requestPathParserResult,
        ControllerMethodAccess access,
        boolean itemOperation, int ids) {
        HttpMethod method = request.getMethod();
        int items = count(parsedRequest.getData());
        if (items == 0) {
            if (method.requiresData()) {
                throw error(request, requestPathParserResult, "DATA_REQUIRED",
                    "Document is required");
            }
        } else {
            if (!method.requiresData()) {
                throw error(request, requestPathParserResult, "DATA_NOT_SUPPORTED",
                    "Document is not supported");
            } else {
                if (items == 1) {
                    if (!itemOperation && !method.supportsCollectionOperation(access)) {
                        throw collectionResourceNotAllowed(request, requestPathParserResult,
                            access);
                    }
                } else if (items > 1) {
                    if (itemOperation || !method.supportsMultiple(access)
                    // array not supported in combination with multiple ids
                            || ids > 1) {
                        throw error(request, requestPathParserResult,
                            "DOCUMENT_ARRAY_NOT_SUPPORTED",
                            "Array of documents not supported");
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

    private static void validateIds(ResourceControllerRequest request,
        RequestPathParserResult pathParserResult,
        ControllerMethodAccess access, int ids) {
        HttpMethod method = request.getMethod();
        if (ids == 1) {
            if (!method.supportsItemOperation(access)) {
                throw itemResourceNotAllowed(request, pathParserResult, access);
            }
        } else if (ids > 1) {
            if (!method.supportsAccessByIds(access)) {
                // if it doesn't support access by id then it is malformed
                if (!method.supportsItemOperation(access)) {
                    throw itemResourceNotAllowed(request, pathParserResult, access);
                } else {
                    throw error(request, pathParserResult, "MULTIPLE_IDS_NOT_SUPPORTED",
                        "Multiple ids not supported");
                }
            } else if (pathParserResult.getRelationship() != null) {
                throw error(request, pathParserResult, "RELATIONSHIP_IDS_NOT_SUPPORTED",
                        "Multiple ids not supported when requesting a relationship");
            }
        } else if (ids == 0) {
            if (pathParserResult.getRelationship() != null) {
                throw error(request, pathParserResult, "RELATIONSHIP_ID_REQUIRED",
                    "ID is required when requesting a relationship");
            }
        }
    }

    private static RequestErrorException error(ResourceControllerRequest request,
        RequestPathParserResult pathParserResult,
        String code, String detail) {
        return error(request, pathParserResult).code(code).title("Not supported").detail(detail)
            .buildException();
    }

    private static RequestError.Builder error(ResourceControllerRequest request,
        RequestPathParserResult pathParserResult) {
        return RequestError.builder().resource(pathParserResult.getResource());
    }

    private static RequestErrorException itemResourceNotAllowed(ResourceControllerRequest request,
        RequestPathParserResult requestPathParserResult,
            ControllerMethodAccess access) {
        List<HttpMethod> supported = new ArrayList<>(HttpMethod.values().length - 1);
        for (HttpMethod m : HttpMethod.values()) {
            if (m.supportsItemOperation(access)) {
                supported.add(m);
            }
        }
        return methodNotAllowed(request, requestPathParserResult, access, supported);
    }

    private static RequestErrorException collectionResourceNotAllowed(
        ResourceControllerRequest request, RequestPathParserResult requestPathParserResult,
            ControllerMethodAccess access) {
        List<HttpMethod> supported = new ArrayList<>(HttpMethod.values().length - 1);
        for (HttpMethod m : HttpMethod.values()) {
            if (m.supportsCollectionOperation(access)) {
                supported.add(m);
            }
        }
        return methodNotAllowed(request, requestPathParserResult, access, supported);
    }

    private static RequestErrorException methodNotAllowed(ResourceControllerRequest request,
        RequestPathParserResult requestPathParserResult,
            ControllerMethodAccess access, List<HttpMethod> supported) {
        return error(request, requestPathParserResult).status(StatusCode.METHOD_NOT_ALLOWED)
                .code(StatusCode.METHOD_NOT_ALLOWED.name()).meta(HttpHeader.Allow.name(), supported)
                // TODO The response MUST query an Allow header containing a list of valid
                // methods for the requested resource.
                .buildException();
    }

    private static ControllerMethodAccess getMethodAccess(ResourceRegistry registry, Resource<?, ?> resource) {
        if (resource != null && resource.getControllerMethodAccess() != null) {
            return resource.getControllerMethodAccess();
        }
        return registry.getSettings().getDefaultControllerAccess();
    }

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

    public Object handleException(ResourceControllerRequest request,
        ResourceControllerResponse response, Exception e) {
        return exceptionHandler.handleException(request, response, e);
    }

    /**
     * Handles exceptions when building {@link ResourceControllerRequest} and handling request
     *
     * @param <T> resource type
     * @param <ID> resource id type
     * @param builder request builder
     * @param response response object
     * @return response
     */
    public <T, ID extends Serializable> Object request(AbstractResourceControllerRequestBuilder<?, ?> builder,
            ResourceControllerResponse response) {
        ResourceControllerRequest request = null;
        try {
            request = builder
                .contentTypeParam(mediaTypeParam)
                .build();
            return request(request, response);
        } catch (Exception e) {
            return handleException(request, response, e);
        }
    }

    <T, ID extends Serializable> Object request(ResourceControllerRequest request,
        ResourceControllerResponse response) {
        RequestPathParserResult requestPathParserResult = requestPathParser
            .parsePath(request); //TODO
        return requestInternal(request, requestPathParserResult, response);
    }

    <T, ID extends Serializable> Object requestInternal(ResourceControllerRequest request,
        RequestPathParserResult requestPathParserResult,
            ResourceControllerResponse response) {
        Assert.notNull(request, "request is required");
        Assert.notNull(response, "response is required");
        Assert.notNull(request.getMethod(), "method is required");

        ControllerMethodAccess access = getMethodAccess(registry,
            requestPathParserResult.getResource());
        int ids = CollectionUtils.size(requestPathParserResult.getIds());
        boolean itemOperation = ids == 1;

        // confirm content type is supported ahead of parsing work
        accept(request, requestPathParserResult.getResource());

        MethodController<T, ID> methodController = getMethodController(request,
            requestPathParserResult, access,
            itemOperation);

        validateIds(request, requestPathParserResult, access, ids);

        // parse the request
        ParsedResourceControllerRequest<T> parsedRequest = parseRequest(request,
            requestPathParserResult);

        // validate data passed in the message body
        validateData(parsedRequest, request, requestPathParserResult, access, itemOperation, ids);

        interceptor.before(parsedRequest);
        Object result;
        try {
            result = methodController.request(parsedRequest, response);
            return contentNegotiator.formatResponse(parsedRequest, response, result);
        } finally {
            interceptor.after(parsedRequest);
        }
    }

    private void accept(ResourceControllerRequest request, Resource resource) {
        if (!contentNegotiator.accept(request)) {
            throw RequestError.builder(resource)
                    .status(StatusCode.UNSUPPORTED_MEDIA_TYPE)
                    .meta(ContentTypeNegotiation.CONTENT_TYPE, request.getContentType())
                    .buildException();
        }
    }

    private MethodController getMethodController(ResourceControllerRequest request,
        RequestPathParserResult requestPathParserResult, ControllerMethodAccess access,
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
            throw itemResourceNotAllowed(request, requestPathParserResult, access);
        } else {
            throw collectionResourceNotAllowed(request, requestPathParserResult, access);
        }
    }

    private <T> ParsedResourceControllerRequest<T> parseRequest(ResourceControllerRequest request,
        RequestPathParserResult requestPathParserResult) {
        Resource<?, ?> resource = requestPathParserResult.getResource();
        ParsedResourceControllerRequest.Builder<T> builder = ParsedResourceControllerRequest.builder(registry,
            request, requestPathParserResult);

        if (hasMediaTypeParam(request)) {
            builder.addAcceptedParameterName(mediaTypeParam);
        }
        requestParser.parse(request, requestPathParserResult, builder);
        if (CollectionUtils.size(requestPathParserResult.getIds()) > 1) {
            // Add filter for ids
            MappedField<?> field = resource.getIdentityField();
            builder
                .addFilter(resource, "pathIds", requestPathParserResult.getIds(),
                    field.getApiName(), Operator.in,
                    requestPathParserResult.getIds());
        }
        if (HttpMethod.PUT == request.getMethod()) {
            // PUT is effectively PATCH all so we have to update all fields
            List<MappedField<?>> fields = resource.getMapping().getAttributes();
            addUpdateFields(builder, fields);
        }
        return builder.build();
    }

    private boolean hasMediaTypeParam(ResourceControllerRequest request) {
        return mediaTypeParam != null && isNotEmpty(request.getParameter(mediaTypeParam));
    }

    public static class Builder {

        private ControllerSettings.Builder settings;
        private List<ResourceControllerBuilderDecorator> resourceControllerBuilderDecorators = new ArrayList<>();

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

        public Builder decorate(ResourceControllerBuilderDecorator... decorators) {
            for (ResourceControllerBuilderDecorator decorator : decorators) {
                resourceControllerBuilderDecorators.add(decorator);
            }
            return me();
        }

        public Builder decorate(Collection<ResourceControllerBuilderDecorator> decorators) {
            resourceControllerBuilderDecorators.addAll(decorators);
            return me();
        }

        public Builder contentNegotiator(ContentNegotiator contentNegotiator) {
            settings.contentNegotiator(contentNegotiator);
            return me();
        }

        public Builder contentNegotiator(ContentNegotiator.Builder contentNegotiator) {
            settings.contentNegotiator(contentNegotiator);
            return me();
        }

        public Builder interceptors(RequestInterceptor... interceptors) {
            settings.interceptors(interceptors);
            return me();
        }

        public Builder requestParser(RequestParser requestParser) {
            settings.requestParser(requestParser);
            return me();
        }

        public Builder requestParser(RequestParser.Builder requestParser) {
            settings.requestParser(requestParser);
            return me();
        }

        public Builder exceptionHandler(ExceptionHandler exceptionHandler) {
            settings.exceptionHandler(exceptionHandler);
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

        public Builder autoDetectDisabled(boolean b) {
            settings.autoDetectDisabled(b);
            return me();
        }

        public Builder mediaTypeParam(String mediaTypeParam) {
            settings.mediaTypeParam(mediaTypeParam);
            return me();
        }

        /**
         * 
         * Use the content negotiator for the specified mediaType as the default content negotiator. This
         * will allow the mediaType to be rendered in a browser by default if desired.
         *
         * @param mediaType default media type
         * @return this builder
         */
        public Builder defaultMediaType(String mediaType) {
            settings.defaultMediaType(mediaType);
            return me();
        }

        /**
         * @see #defaultMediaType(String)
         * @param mediaType type of media
         * @return this builder
         */
        public Builder defaultMediaType(MediaType mediaType) {
            return defaultMediaType(mediaType.getContentType());
        }

        public ResourceController build() {
            resourceControllerBuilderDecorators.stream().forEach(d -> d.decorate(this));
            return new ResourceController(settings);
        }
    }

}
