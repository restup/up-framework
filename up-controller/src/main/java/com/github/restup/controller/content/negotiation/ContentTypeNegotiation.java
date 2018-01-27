package com.github.restup.controller.content.negotiation;

import com.github.restup.controller.model.MediaType;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerResponse;
import java.util.Objects;

public abstract class ContentTypeNegotiation implements ContentNegotiator {

    public final static String CONTENT_TYPE = "Content-Type";
    private final String contentType;
    private final String headerValue;

    public ContentTypeNegotiation(MediaType contentType) {
        this(contentType.getContentType());
    }

    public ContentTypeNegotiation(String contentType) {
        super();
        this.contentType = contentType;
        this.headerValue = this.contentType;
    }

    @Override
    public <T> boolean accept(ResourceControllerRequest request) {
        return Objects.equals(request.getContentType(), contentType);
    }

    @Override
    public final <T> Object formatResponse(ParsedResourceControllerRequest<T> request, ResourceControllerResponse response,
            Object result) {
        response.setHeader(CONTENT_TYPE, headerValue);
        return format(request, response, result);
    }

    abstract Object format(ParsedResourceControllerRequest<?> request, ResourceControllerResponse response, Object result);

    public String getContentType() {
        return contentType;
    }
}
