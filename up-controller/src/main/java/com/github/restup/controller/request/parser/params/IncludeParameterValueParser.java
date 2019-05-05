package com.github.restup.controller.request.parser.params;

import static com.github.restup.controller.request.parser.params.ParameterParserFactory.ParameterParserFactories.IncludeValueParameterParserFactory;
import static java.lang.reflect.Array.getLength;

import com.github.restup.controller.request.parser.params.ParameterParser.ParameterParsers;
import com.github.restup.query.ResourceQueryStatement.Type;
import com.github.restup.registry.Resource;

/**
 * <p> Parses include parameters <p> <p> Ex: For a resource with relationships to resources named
 * foo &amp; bar </p>
 *
 * <pre>
 * ?include=foo,bar
 * </pre>
 * <p> Since a resource may have multiple relationships to a resource, the path may be targeted as
 * well. For example, given a resource Foo with goodBarId and badBarId fields both with a {@link
 * com.github.restup.registry.ResourceRelationship} to Bar, the field(s) to join on may be specified
 * in [] </p>
 *
 * <pre>
 * /foo?include=bar[goodBarId]
 * /bar?include=foo[goodBarId]
 * </pre>
 *
 * @author abuttaro
 */
public class IncludeParameterValueParser implements ParameterValueParser<String> {

    private final ParameterParser parser;

    // TODO nested includes /foo?include[bar]=boo


    private IncludeParameterValueParser(ParameterParser parser) {
        this.parser = parser;
    }

    static IncludeParameterValueParser forParser(ParameterParser parser) {
        ParameterParser resultParser = parser;
        if (parser instanceof ParameterParsers) {
            resultParser = IncludeValueParameterParserFactory.convert(parser);
        }
        return new IncludeParameterValueParser(resultParser);
    }

    @Override
    public void parse(ParameterParsingContext ctx, ParameterParserResult ppresult,
        String rawValue, String parameterValue) {

        ParameterParserResult result = parser.parse(ctx, parameterValue);

        Resource<?, ?> resource = result.getResource();
        String[] tokens = result.getTokens();
        ctx.getBuilder().setFieldRequest(resource, Type.Default);
        if (getLength(tokens) == 1) {
            ctx.getBuilder()
                .addIncludeJoinPaths(resource, tokens[0]);
        }
    }

}
