package com.github.restup.controller.request.parser.params;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;

/**
 * Similar to offset, but pageNumber is 1 based, so the value has to be adjusted <p> <p> Ex: <p>
 * <pre>
 * ?pageNumber=1
 * </pre>
 *
 * @author abuttaro
 */
public class PageNumberParser extends AbstractIntegerParser {

    public PageNumberParser() {
        this("pageNumber");
    }

    public PageNumberParser(String parameterName) {
        this(parameterName, false, false);
    }

    public PageNumberParser(String parameterName, boolean ignoreNull, boolean ignoreBlank) {
        super(parameterName, ignoreNull, ignoreBlank);
    }

    @Override
    public <T> void apply(ResourceControllerRequest details, ParsedResourceControllerRequest.Builder<T> builder, String parameterName, Integer value) {
        //TODO support pageNumber[resource] for paginated query
        builder.setPageOffset(parameterName, adjusted(value), true);
    }

    private Integer adjusted(Integer value) {
        if (value != null && value > 0) {
            return value - 1;
        }
        return value;
    }

}
