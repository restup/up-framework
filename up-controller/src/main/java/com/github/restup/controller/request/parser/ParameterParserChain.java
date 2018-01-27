package com.github.restup.controller.request.parser;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.request.parser.params.FieldsParser;
import com.github.restup.controller.request.parser.params.FilterParser;
import com.github.restup.controller.request.parser.params.IncludeParser;
import com.github.restup.controller.request.parser.params.PageLimitParser;
import com.github.restup.controller.request.parser.params.PageNumberParser;
import com.github.restup.controller.request.parser.params.PageOffsetParser;
import com.github.restup.controller.request.parser.params.SortParamParser;
import com.github.restup.util.Assert;
import java.util.List;

/**
 * Iterates over all parameter names executing the first {@link RequestParamParser} that accepts the parameter for each
 *
 * @author abuttaro
 */
public class ParameterParserChain implements RequestParser {

    private final RequestParamParser[] parsers;

    public ParameterParserChain(RequestParamParser... parsers) {
        super();
        Assert.notEmpty("parsers are required", parsers);
        this.parsers = parsers;
    }

    /**
     * By default adds {@link PageOffsetParser}, {@link PageLimitParser} , {@link SortParamParser}, {@link FilterParser}, {@link IncludeParser} , {@link FieldsParser} and adds a number of fuzzy parameter parsers as well: {@link PageNumberParser} for "page", "count", "pageNo", "pageNum", "pageNumber" parameters and {@link PageOffsetParser} for "start" parameters and {@link PageLimitParser} with "rpp" and "pageSize" parameters and {@link FilterParser} for "f" and "q" filter parameters
     */
    public ParameterParserChain() {
        this(new PageOffsetParser()
                , new PageLimitParser()
                , new SortParamParser()
                , new FilterParser()
                , new IncludeParser()
                , new FieldsParser()
                // support some fuzziness in accepted paging params
                // page / rpp twitter
                , new PageNumberParser("page")
                , new PageLimitParser("rpp") // records per page
                // start / count LinkedIn
//				, new PageOffsetParser("start")
//				, new PageNumberParser("count")
                // other
                , new PageNumberParser()
                , new PageLimitParser("pageSize")
                , new PageNumberParser("pageNo")
                , new PageNumberParser("pageNum")
                // some fuzzy support for filters
                , new FilterParser("f")
                , new FilterParser("q"));
    }

    /**
     * Iterate over the {@link #parsers} and execute the first which accepts the parameter
     */
    @Override
    public void parse(ResourceControllerRequest request, ParsedResourceControllerRequest.Builder<?> builder) {
        List<String> params = request.getParameterNames();
        if (params != null) {
            for (String param : params) {
                if (param != null) {
                    for (RequestParamParser parser : parsers) {
                        if (parser.accept(param)) {
                            builder.addAcceptedParameterName(param);
                            parser.parse(request, builder, param, request.getParameter(param));
                            break;
                        }
                    }
                }
            }
        }
    }

}
