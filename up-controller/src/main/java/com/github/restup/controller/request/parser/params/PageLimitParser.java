package com.github.restup.controller.request.parser.params;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;

/**
 * Parses an integer limit for number of records returned by pagination. <p> <p> Ex: <p>
 * <pre>
 * ?limit=100
 * </pre>
 *
 * @author abuttaro
 */
public class PageLimitParser extends AbstractIntegerParser {

    public static final String LIMIT = "limit";

    public PageLimitParser() {
        this(LIMIT);
    }

    public PageLimitParser(String parameterName) {
        this(parameterName, false, false);
    }

    public PageLimitParser(String parameterName, boolean ignoreNull, boolean ignoreBlank) {
        super(parameterName, ignoreNull, ignoreBlank);
    }

    @Override
    public <T> void apply(ResourceControllerRequest details, ParsedResourceControllerRequest.Builder<T> builder, String parameterName, Integer value) {
        //TODO support limit[resource] for paginated query
        builder.setPageLimit(parameterName, value);
    }

}
