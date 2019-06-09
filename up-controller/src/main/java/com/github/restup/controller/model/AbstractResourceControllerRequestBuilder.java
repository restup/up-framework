package com.github.restup.controller.model;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.github.restup.registry.ResourceRegistry;
import com.github.restup.service.model.ResourceData;
import java.util.function.Supplier;

public abstract class AbstractResourceControllerRequestBuilder<T extends AbstractResourceControllerRequestBuilder<T, R>, R extends ResourceControllerRequest> {

    protected HttpMethod method;
    protected ResourceData<?> body;
    protected String baseRequestUrl;
    protected String requestPath;
    protected String contentTypeParam;
    private ResourceRegistry registry;

    public abstract R build();

    protected T me() {
        return (T) this;
    }

    public T registry(ResourceRegistry registry) {
        this.registry = registry;
        return me();
    }

    public T baseRequestUrl(String baseRequestUrl) {
        this.baseRequestUrl = baseRequestUrl;
        return me();
    }

    public T requestPath(String requestPath) {
        this.requestPath = requestPath;
        return me();
    }

    public T contentTypeParam(String contentTypeParam) {
        this.contentTypeParam = contentTypeParam;
        return me();
    }

    public T body(ResourceData<?> body) {
        this.body = body;
        return me();
    }

    public T method(HttpMethod method) {
        this.method = method;
        return me();
    }

    protected String getContentType(Supplier<String[]> contentTypeFromParamSupplier,
        Supplier<String> contentTypeFromRequestSupplier) {
        if (contentTypeParam != null) {
            String[] arr = contentTypeFromParamSupplier.get();
            if (arr != null) {
                for (String s : arr) {
                    if (isNotBlank(s)) {
                        return s;
                    }
                }
            }
        }
        return contentTypeFromRequestSupplier.get();
    }

}