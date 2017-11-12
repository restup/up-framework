package com.github.restup.controller.request.parser.params;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;

/**
 * Parses an integer offset for pagination. Named offset by default
 * <p>
 * <p>
 * Ex:
 * <p>
 * <pre>
 * ?offset=0
 * </pre>
 *
 * @author abuttaro
 */
public class PageOffsetParser extends AbstractIntegerParser {

    public static final String OFFSET = "offset";

    public PageOffsetParser() {
        this(OFFSET);
    }

    public PageOffsetParser(String parameterName) {
        this(parameterName, false, false);
    }

    public PageOffsetParser(String parameterName, boolean ignoreNull, boolean ignoreBlank) {
        super(parameterName, ignoreNull, ignoreBlank);
    }

    @Override
    public <T> void apply(ResourceControllerRequest details, ParsedResourceControllerRequest.Builder<T> builder, String parameterName, Integer value) {
        //TODO support offset[resource] for paginated query
        builder.setPageOffset(parameterName, value);
    }

}
