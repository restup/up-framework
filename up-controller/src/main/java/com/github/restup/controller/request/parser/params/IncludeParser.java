package com.github.restup.controller.request.parser.params;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.query.ResourceQueryStatement.Type;

/**
 * <p>Parses include parameters</p>
 * <p>Ex: For a resource with relationships to resources named foo &amp; bar</p>
 * 
 * <pre>
 * ?include=foo,bar
 * </pre>
 * <p>
 * Since a resource may have multiple relationships to a resource, the path may be targeted as well.
 * For example, given a resource Foo with goodBarId and badBarId fields both with a
 * {@link com.github.restup.annotations.field.Relationship} to Bar, the field(s) to join on may be specified in []
 * </p>
 * 
 * <pre>
 * /foo?include=bar[goodBarId]
 * /bar?include=foo[goodBarId]
 * </pre>
 *
 * @author abuttaro
 */
public class IncludeParser extends AbstractDelimitedParamParser<String> {

    // TODO nested includes /foo?include[bar]=boo

    public IncludeParser() {
        this("include");
    }

    public IncludeParser(String parameterName) {
        this(parameterName, ",", false, false);
    }

    public IncludeParser(String parameterName, String delimiter, boolean ignoreNull, boolean ignoreBlank) {
        super(parameterName, delimiter, ignoreNull, ignoreBlank);
    }

    @Override
    public <T> void applyDelimitedValue(ResourceControllerRequest details, ParsedResourceControllerRequest.Builder<T> builder, String parsedParameter,
            String value, String rawParamName, String rawValue) {
        String[] parts = parseBracketedString(builder, rawParamName, rawValue, value, 1, 2);
        if (parts != null) {
            String resource = parts[0];
            builder.setFieldRequest(rawParamName, rawValue, resource, Type.Default);
            if (parts.length == 2) {
                builder.addIncludeJoinPaths(rawParamName, rawValue, resource, parts[1]);
            }
        }
    }

}