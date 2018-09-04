package com.github.restup.controller.settings;

import com.github.restup.controller.ExceptionHandler;
import com.github.restup.controller.content.negotiation.ContentNegotiator;
import com.github.restup.controller.interceptor.RequestInterceptor;
import com.github.restup.controller.request.parser.RequestParser;
import com.github.restup.registry.ResourceRegistry;

public class BasicControllerSettings implements ControllerSettings {

    private final ResourceRegistry registry;
    private final ContentNegotiator contentNegotiator;
    private final RequestInterceptor requestInterceptor;
    private final RequestParser requestParser;
    private final ExceptionHandler exceptionHandler;
    private final String defaultMediaType;
    private final String mediaTypeParam;

    protected BasicControllerSettings(ResourceRegistry registry, ContentNegotiator contentNegotiator,
            RequestInterceptor requestInterceptor, RequestParser requestParsers,
        ExceptionHandler exceptionHandler, String defaultMediaType, String mediaTypeParam) {
        super();
        this.registry = registry;
        this.contentNegotiator = contentNegotiator;
        this.requestInterceptor = requestInterceptor;
        requestParser = requestParsers;
        this.exceptionHandler = exceptionHandler;
        this.defaultMediaType = defaultMediaType;
        this.mediaTypeParam = mediaTypeParam;
    }

    @Override
    public String getDefaultMediaType() {
        return defaultMediaType;
    }

    @Override
    public String getMediaTypeParam() {
        return mediaTypeParam;
    }

    @Override
    public ResourceRegistry getRegistry() {
        return registry;
    }

    @Override
    public ContentNegotiator getContentNegotiator() {
        return contentNegotiator;
    }

    @Override
    public RequestInterceptor getRequestInterceptor() {
        return requestInterceptor;
    }

    @Override
    public RequestParser getRequestParser() {
        return requestParser;
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

}
