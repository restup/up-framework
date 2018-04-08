package com.github.restup.controller.request.parser.params;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.request.parser.RequestParamParser;
import com.github.restup.util.Assert;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

/**
 * provides base implementation and support for parameter parsing
 * 
 * @param <P> parameter type
 */
public abstract class AbstractRequestParamParser<P> implements RequestParamParser {

    private final String parameterName;
    private final boolean ignoreNull;
    private final boolean ignoreBlank;

    protected AbstractRequestParamParser(String parameterName, boolean ignoreNull, boolean ignoreBlank) {
        super();
        Assert.notNull(parameterName, "parameterName is required");
        this.parameterName = parameterName;
        this.ignoreNull = ignoreNull;
        this.ignoreBlank = ignoreBlank;
    }

    protected AbstractRequestParamParser(String parameterName) {
        this(parameterName, false, false);
    }

    /**
     * parses a parameter with brackets to a String array, validating the number of resulting parts.
     * <p>
     * ex filter[foo][gt] -&gt; ['filter', 'foo', 'gt' ]
     * 
     * @param <T> resource type
     * @param builder request builder
     * @param rawParameterName to be parsed
     * @param rawValue raw parameter value
     * @param targetString parameter to parse
     * @param minPartSize minimum allowed paths
     * @param maxPartSize maximum allowed paths
     * @return null if any errors occur and are added to builder, path parts otherwise
     */
    public static <T> String[] parseBracketedString(ParsedResourceControllerRequest.Builder<T> builder, String rawParameterName, Object rawValue, String targetString, int minPartSize, int maxPartSize) {
        String[] parts = targetString.split("\\[");
        if (parts.length < minPartSize
                || parts.length > maxPartSize || (parts.length > 1 && !hasTrailingBracket(targetString))) {
            //TODO more error detail?
            builder.addParameterError(rawParameterName, rawValue);
            return null;
        } else {
            for (int i = 1; i < parts.length; i++) {
                parts[i] = removeBrackets(parts[i]);
            }
            return parts;
        }
    }

    static boolean hasTrailingBracket(String s) {
        return s.length() > 1 && s.endsWith("]");
    }

    static String removeBrackets(String s) {
        if (hasTrailingBracket(s)) {
            return s.substring(0, s.length() - 1);
        }
        return null;
    }

    /*TODO
     * @return null if any errors with parameter. Any other value will be passed to #app
     */
    protected <T> P getParsedParameter(ResourceControllerRequest details, ParsedResourceControllerRequest.Builder<T> builder, String parameterName, String[] parameterValues) {
        return (P) parameterName;
    }

    /*TODO
     * Apply the param/value to builder
     */
    abstract <T> void apply(ResourceControllerRequest details, ParsedResourceControllerRequest.Builder<T> builder, P parsedParameterName, String value, String rawParamName, String rawValue);

    @Override
    public <T> void parse(ResourceControllerRequest details, ParsedResourceControllerRequest.Builder<T> builder, final String rawParamName, String[] parameterValues) {
        if (parameterValues == null || parameterValues.length < 1) {
            // apply with null to handle empty param error
            this.applyNonNull(builder, rawParamName);
        } else {
            // parse param (if necessary) and continue if non null
            P parsedParameterName = this
                .getParsedParameter(details, builder, rawParamName, parameterValues);
            if (parsedParameterName != null) {
                // process each value
                for (String rawValue : parameterValues) {
                    if (StringUtils.isBlank(rawValue)) {
                        // add error if blank unless set to ignore blank values
                        if (!this.isIgnoreBlank()) {
                            builder.addParameterError(rawParamName, rawValue);
                        }
                    } else {
                        // apply param/value to builder
                        this.apply(details, builder, parsedParameterName, rawValue.trim(),
                            rawParamName, rawValue);
                    }
                }
            }
        }
    }

    protected <T> void applyNonNull(ParsedResourceControllerRequest.Builder<T> builder, String paramName) {
        if (!this.ignoreNull) {
            builder.addParameterError(paramName, null);
        }
    }

    @Override
    public boolean accept(String paramName) {
        return Objects.equals(paramName, this.parameterName);
    }

    public String getParameterName() {
        return this.parameterName;
    }

    public boolean isIgnoreBlank() {
        return this.ignoreBlank;
    }

}
