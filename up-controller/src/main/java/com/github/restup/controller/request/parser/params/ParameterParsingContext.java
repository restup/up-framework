package com.github.restup.controller.request.parser.params;

import com.github.restup.controller.model.ParsedResourceControllerRequest.Builder;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.errors.RequestError;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;

public class ParameterParsingContext<T> {

    private final ResourceControllerRequest request;
    private final Builder<T> builder;
    private final String rawParameterName;
    private final String[] rawParameterValues;

    private ParameterParsingContext(
        ResourceControllerRequest request,
        Builder<T> builder, String rawParameterName, String[] rawParameterValues) {
        this.request = request;
        this.builder = builder;
        this.rawParameterName = rawParameterName;
        this.rawParameterValues = rawParameterValues;
    }

    public static <T> ParameterParsingContext of(ResourceControllerRequest request,
        Builder<T> builder,
        String parameterName, String[] parameterValues) {
        return new ParameterParsingContext(request, builder, parameterName, parameterValues);
    }

    public static RequestError.Builder parameterError(ParameterParsingContext ctx, Object value) {
        return Builder.getParameterError(ctx.getBuilder(), ctx.getRawParameterName(), value);
    }

    public ResourceControllerRequest getRequest() {
        return request;
    }

    public Builder<T> getBuilder() {
        return builder;
    }

    public String getRawParameterName() {
        return rawParameterName;
    }

    public String[] getRawParameterValues() {
        return rawParameterValues;
    }

    public void addParameterError(String parameterValue) {
        builder.addParameterError(getRawParameterName(), parameterValue);
    }

    public void addError(RequestError.Builder error) {
        builder.addError(error);
    }

    public Resource getResource(String resourceName) {
        ResourceRegistry registry = getRequest().getResource().getRegistry();
        return registry.getResource(resourceName);
    }
}
