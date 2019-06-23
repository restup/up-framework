package com.github.restup.controller.settings;

import com.github.restup.controller.ExceptionHandler;
import com.github.restup.controller.content.negotiation.ContentNegotiator;
import com.github.restup.controller.interceptor.RequestInterceptor;
import com.github.restup.controller.linking.LinkBuilderFactory;
import com.github.restup.controller.request.parser.RequestParser;
import com.github.restup.controller.request.parser.path.RequestPathParser;
import com.github.restup.registry.ResourceRegistry;

public class BasicControllerSettings implements ControllerSettings {

    private final ResourceRegistry registry;
    private final ContentNegotiator contentNegotiator;
    private final RequestInterceptor requestInterceptor;
    private final RequestParser requestParser;
    private final RequestPathParser requestPathParser;
    private final ExceptionHandler exceptionHandler;
    private final String defaultMediaType;
    private final String mediaTypeParam;
    private final LinkBuilderFactory linkBuilderFactory;

    protected BasicControllerSettings(ResourceRegistry registry, ContentNegotiator contentNegotiator,
        RequestInterceptor requestInterceptor, RequestParser requestParser,
        RequestPathParser requestPathParser,
        ExceptionHandler exceptionHandler, String defaultMediaType, String mediaTypeParam,
        LinkBuilderFactory linkBuilderFactory) {
        super();
        this.registry = registry;
        this.contentNegotiator = contentNegotiator;
        this.requestInterceptor = requestInterceptor;
        this.requestParser = requestParser;
        this.requestPathParser = requestPathParser;
        this.exceptionHandler = exceptionHandler;
        this.defaultMediaType = defaultMediaType;
        this.mediaTypeParam = mediaTypeParam;
        this.linkBuilderFactory = linkBuilderFactory;
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
    public RequestPathParser getRequestPathParser() {
        return requestPathParser;
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    @Override
    public LinkBuilderFactory getLinkBuilderFactory() {
        return linkBuilderFactory;
    }

}
