package com.github.restup.controller.request.parser.params;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;

/**
 * <p>
 * Parses filter parameters.
 * </p>
 * 
 * <p>
 * Ex: For a resource with fields named foo &amp; bar
 * </p>
 * 
 * <pre>
 * ?filter[foo]=foo
 * ?filter[bar][gt]=1
 * </pre>
 * 
 * 
 * @author abuttaro
 */
public class FilterParser extends AbstractRequestParamParser<String[]> {

    private final String startsWith;

    public FilterParser() {
        this("filter");
    }

    public FilterParser(String parameterName) {
        super(parameterName);
        startsWith = parameterName + "[";
    }

    @Override
    public <T> void apply(ResourceControllerRequest details, ParsedResourceControllerRequest.Builder<T> builder, String[] parsedParmeterName, String value, final String rawParamName, final String rawValue) {
        String field = parsedParmeterName[1];
        String operator = parsedParmeterName.length == 3 ? parsedParmeterName[2] : null;
        if (field == null || (operator == null && parsedParmeterName.length == 3)) {
            builder.addParameterError(rawParamName, rawValue);
        } else {
            builder.addFilter(rawParamName, rawValue, field, operator, value);
        }
    }

    @Override
    protected <T> String[] getParsedParameter(ResourceControllerRequest details, ParsedResourceControllerRequest.Builder<T> builder, String parameterName,
            String[] parameterValues) {
        String[] parts = parseBracketedString(builder, parameterName, parameterValues, parameterName, 2, 3);
        return parts;
    }

    @Override
    public boolean accept(String paramName) {
        return paramName.startsWith(startsWith);
    }

}
