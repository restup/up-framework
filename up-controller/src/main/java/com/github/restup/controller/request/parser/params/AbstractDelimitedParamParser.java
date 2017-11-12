package com.github.restup.controller.request.parser.params;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.util.Assert;
import org.apache.commons.lang3.StringUtils;

/**
 * Provides support for parameters that may contain a delimited list
 *
 * @param <P>
 */
public abstract class AbstractDelimitedParamParser<P> extends AbstractRequestParamParser<P> {

    private final String delimiter;

    public AbstractDelimitedParamParser(String parameterName, String delimiter, boolean ignoreNull,
                                        boolean ignoreBlank) {
        super(parameterName, ignoreNull, ignoreBlank);
        Assert.notNull(delimiter, "delimiter is required");
        this.delimiter = delimiter;
    }

    @Override
    public final <T> void apply(ResourceControllerRequest request, ParsedResourceControllerRequest.Builder<T> builder, P parsedParameterName, String parameterValue,
                                final String rawParamName, final String rawValue) {
        String[] values = parameterValue.split(delimiter);
        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                if (!isIgnoreBlank()) {
                    builder.addParameterError(rawParamName, rawValue);
                }
            } else {
                applyDelimitedValue(request, builder, parsedParameterName, value.trim(), rawParamName, rawValue);
            }
        }
    }

    abstract <T> void applyDelimitedValue(ResourceControllerRequest request, ParsedResourceControllerRequest.Builder<T> builder, P parsedParameterName,
                                          String value, String rawParamName, String rawValue);
}
