package com.github.restup.controller.request.parser.params;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;

/**
 * Provides base implementation for Integer parameters (paging parameters).
 */
public abstract class AbstractIntegerParser extends AbstractRequestParamParser<String> {

    public AbstractIntegerParser(String parameterName, boolean ignoreNull, boolean ignoreBlank) {
        super(parameterName, ignoreNull, ignoreBlank);
    }

    abstract <T> void apply(ResourceControllerRequest request, ParsedResourceControllerRequest.Builder<T> builder, String parameterName, Integer value);

    @Override
    public <T> void apply(ResourceControllerRequest details, ParsedResourceControllerRequest.Builder<T> builder, String parameterName, String value, final String rawParamName, final String rawValue) {
        try {
            apply(details, builder, parameterName, Integer.valueOf(value));
        } catch (NumberFormatException ex) {
            builder.addParameterError(rawParamName, rawValue);
        }
    }

}
