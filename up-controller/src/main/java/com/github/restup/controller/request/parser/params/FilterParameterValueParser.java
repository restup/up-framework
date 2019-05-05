package com.github.restup.controller.request.parser.params;

import com.github.restup.registry.Resource;
import java.util.Objects;

/**
 * <p> Parses filter parameters. </p>
 *
 * <p> Ex: For a resource with fields named foo &amp; bar </p>
 *
 * <pre>
 * ?filter[foo]=foo
 * ?filter[bar][gt]=1
 * </pre>
 *
 * @author abuttaro
 */
public class FilterParameterValueParser implements ParameterValueParser<String> {


    public FilterParameterValueParser() {
    }

    static String getValue(int fieldIndex, String... tokens) {
        return tokens.length > fieldIndex ? tokens[fieldIndex] : null;
    }

    @Override
    public void parse(ParameterParsingContext ctx, ParameterParserResult result,
        String rawValue, String parameterValue) {

        String[] tokens = result.getTokens();

        int fieldIndex = 0;
        String resourceOrField = getValue(0, tokens);

        Resource resource = result.getResource();
        if (Objects.equals(resource.getName(), resourceOrField)) {
            fieldIndex = 1;
        }

        int operatorIndex = fieldIndex + 1;
        String field = getValue(fieldIndex, tokens);

        String operator = getValue(operatorIndex, tokens);
        ctx.getBuilder()
            .addFilter(resource, ctx.getRawParameterName(), rawValue, field, operator,
                parameterValue);
    }


}
