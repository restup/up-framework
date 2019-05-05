package com.github.restup.controller.request.parser;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.util.Assert;
import java.util.List;

/**
 * Iterates over all parameter names executing the first {@link RequestParamParser} that accepts the parameter for each
 *
 * @author abuttaro
 */
public class ParameterParserChain implements RequestParser {

    private final RequestParamParser[] parsers;

    ParameterParserChain(RequestParamParser... parsers) {
        super();
        Assert.notEmpty("parsers are required", parsers);
        this.parsers = parsers;
    }

    public static ParameterParserChain of(List<RequestParamParser> parsers) {
        return of(parsers.toArray(new RequestParamParser[parsers.size()]));
    }

    public static ParameterParserChain of(RequestParamParser... parsers) {
        return new ParameterParserChain(parsers);
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
                            builder.addAcceptedResourceParameterName(param);
                            parser.parse(request, builder, param, request.getParameter(param));
                            break;
                        }
                    }
                }
            }
        }
    }

    RequestParamParser[] getParsers() {
        return parsers;
    }

}
