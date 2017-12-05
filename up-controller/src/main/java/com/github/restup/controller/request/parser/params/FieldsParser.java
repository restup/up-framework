package com.github.restup.controller.request.parser.params;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.query.ResourceQueryStatement.Type;
import com.github.restup.registry.Resource;

/**
 * Parses fields parameters. <p> Ex: For a resource foo with fields named a, b, & c
 * <pre>
 * ?fields=a,b
 * ?filter[foo]=a,b
 * </pre>
 *
 * @author abuttaro
 */
public class FieldsParser extends AbstractDelimitedParamParser<Object[]> {

    public FieldsParser() {
        this("fields");
    }

    public FieldsParser(String parameterName) {
        this(parameterName, ",", false, false);
    }

    public FieldsParser(String parameterName, String delimiter, boolean ignoreNull, boolean ignoreBlank) {
        super(parameterName, delimiter, ignoreNull, ignoreBlank);
    }

    @Override
    protected <T> Object[] getParsedParameter(ResourceControllerRequest details, ParsedResourceControllerRequest.Builder<T> builder, String parameterName,
            String[] parameterValues) {
        Object value = parameterValues.length == 1 ? parameterValues[0] : parameterValues;
        Object[] parts = parseBracketedString(builder, parameterName, value, parameterName, 1, 2);
        return parts;
    }

    @Override
    public <T> void applyDelimitedValue(ResourceControllerRequest details, ParsedResourceControllerRequest.Builder<T> builder, Object[] parts, String parameterValue, String rawParamName, String rawValue) {
        String resource = getResource(details.getResource(), parts);
        if (parameterValue.equals("*")) {
            builder.setFieldRequest(rawParamName, rawValue, resource, Type.All);
        } else if (parameterValue.equals("**")) {
            builder.setFieldRequest(rawParamName, rawValue, resource, Type.Every);
        } else if (parameterValue.startsWith("+")) {
            builder.addAdditionalField(rawParamName, rawValue, resource, parameterValue.substring(1));
        } else if (parameterValue.startsWith("-")) {
            builder.addExcludedField(rawParamName, rawValue, resource, parameterValue.substring(1));
        } else {
            builder.addRequestedField(rawParamName, rawValue, resource, parameterValue);
        }
    }

    private String getResource(Resource<?, ?> resource, Object[] parts) {
        if (parts.length == 2) {
            return (String) parts[1];
        }
        return resource.getName();
    }

    @Override
    public boolean accept(String paramName) {
        return paramName.startsWith(getParameterName());
    }

}
