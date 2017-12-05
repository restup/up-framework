package com.github.restup.controller.request.parser.params;

import com.github.restup.controller.model.ParsedResourceControllerRequest.Builder;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.query.ResourceSort;

/**
 * Parses a parameter of delimited field names specifying sort order. ',' delimited and named sort by default. By default sort order is ascending, but can be changed by using '-' prefix ahead of a field name <p> <p> Ex: For a resource with fields named foo & bar <p>
 * <pre>
 * ?sort=foo,bar
 * ?sort=+foo,-bar
 * </pre>
 *
 * @author abuttaro
 */
public class SortParamParser extends AbstractDelimitedParamParser<String> {

    public SortParamParser() {
        this("sort");
    }

    public SortParamParser(String parameterName) {
        this(parameterName, ",", false, false);
    }

    public SortParamParser(String parameterName, String delimiter, boolean ignoreNull, boolean ignoreBlank) {
        super(parameterName, delimiter, ignoreNull, ignoreBlank);
    }

    @Override
    public <T> void applyDelimitedValue(ResourceControllerRequest details, Builder<T> builder, String paramName, String parameterValue, final String rawParamName, final String rawValue) {
        // check if it is +/- or unspecified
        // TODO support +/- indicators?
        Boolean asc = ResourceSort.isAscending(parameterValue.charAt(0));
        // parse out +/- if necessary
        String field = asc == null ? parameterValue : parameterValue.substring(1);
        // add parameter
        builder.addSort(rawParamName, rawValue, field, asc);
    }

}
