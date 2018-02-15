package com.github.restup.controller.settings;

import com.github.restup.controller.ExceptionHandler;
import com.github.restup.controller.content.negotiation.ContentNegotiator;
import com.github.restup.controller.interceptor.RequestInterceptor;
import com.github.restup.controller.request.parser.RequestParser;
import com.github.restup.registry.ResourceRegistry;

public class BasicControllerSettings implements ControllerSettings {

    private final ResourceRegistry registry;
    private final ContentNegotiator[] contentNegotiators;
    private final RequestInterceptor requestInterceptor;
    private final RequestParser requestParser;
    private final ExceptionHandler exceptionHandler;
    private final String defaultMediaType;

    protected BasicControllerSettings(ResourceRegistry registry, ContentNegotiator[] contentNegotiators,
            RequestInterceptor requestInterceptor, RequestParser requestParsers,
            ExceptionHandler exceptionHandler, String defaultMediaType) {
        super();
        this.registry = registry;
        this.contentNegotiators = contentNegotiators;
        this.requestInterceptor = requestInterceptor;
        this.requestParser = requestParsers;
        this.exceptionHandler = exceptionHandler;
        this.defaultMediaType = defaultMediaType;
    }

    @Override
    public String getDefaultMediaType() {
        return defaultMediaType;
    }

    @Override
    public ResourceRegistry getRegistry() {
        return registry;
    }

    @Override
    public ContentNegotiator[] getContentNegotiators() {
        return contentNegotiators;
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
