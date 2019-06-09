package com.github.restup.controller.request.parser.params;

import static com.github.restup.controller.request.parser.params.ParameterResourceParser.ParameterResourceParsers.Parsed;

import com.github.restup.controller.request.parser.params.ParameterParser.ParameterParsers;
import com.github.restup.registry.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * <p>{@link ParameterParsers} will work for most parameters, however there are some exceptions. So
 * that these exceptions do not add to configuration complexity, these factories provided parameter
 * specific implementations corresponding to {@link ParameterParser} configured.</p>
 *
 * <p>For example, filter[resource][field][gt] requires some additional logic than the default
 * Bracketed implementation to parse out resource correctly.</p>
 */
public interface ParameterParserFactory {

    /**
     * @param parser configured
     * @return supplier of the {@link ParameterParser} implementation
     */
    Supplier<ParameterParser> getFactoryMethod(ParameterParser parser);

    default ParameterParser convert(ParameterParser parser) {
        Supplier<ParameterParser> factoryMethod = getFactoryMethod(parser);
        if (factoryMethod == null) {
            throw new UnsupportedOperationException("not supported");
        }
        return factoryMethod.get();
    }

    enum ParameterParserFactories implements ParameterParserFactory {
        /**
         * Include parameter value factory to support include=resource[field]
         */
        IncludeValueParameterParserFactory {

            private final Map<ParameterParser, Supplier<ParameterParser>> map;

            {
                Map<ParameterParser, Supplier<ParameterParser>> map = new HashMap<>();
                map.put(ParameterParsers.Bracketed, this::getBracketed);
                map.put(ParameterParsers.Unparsed, this::getUnparsed);
                this.map = Collections.unmodifiableMap(map);
            }

            private ParameterParser getBracketed() {
                return new BracketedParameterParser(
                    BracketedParameterParser::parseResourceBeforeBrackets);
            }

            private ParameterParser getUnparsed() {
                return (ctx, value) -> ParameterParserResult.of(Parsed.parseResource(ctx, value));
            }

            @Override
            public Supplier<ParameterParser> getFactoryMethod(ParameterParser parser) {
                return map.get(parser);
            }
        },

        /**
         * <p>Filter parameter name parser to support parsing resource correctly when resource is
         * the requested resource or an included resource. <ul><li>[field]=1</li>
         * <li>[field][gt]=1</li> <li>[resource][field]=1</li> <li>[resource][field][gt]=1</li>
         * </ul> </p>
         */
        FilterNameParameterParserFactory {

            private final Map<ParameterParser, Supplier<ParameterParser>> map;

            {
                Map<ParameterParser, Supplier<ParameterParser>> map = new HashMap<>();
                map.put(ParameterParsers.Bracketed, this::getBracketed);
                map.put(ParameterParsers.Unparsed, () -> ParameterParsers.Unparsed);
                this.map = Collections.unmodifiableMap(map);
            }

            private ParameterParser getBracketed() {
                return new BracketedParameterParser(this::parseResource);
            }

            public Resource parseResource(ParameterParsingContext ctx, String name,
                String... tokens) {

                // if 1 token, just a field, defaults to [eq]

                if (tokens.length > 1) {
                    Resource resource = ctx.getResource(tokens[0]);
                    if (tokens.length == 3 || resource != null) {
                        return resource;
                    }
                }

                return ctx.getResource();
            }

            @Override
            public Supplier<ParameterParser> getFactoryMethod(ParameterParser parser) {
                return map.get(parser);
            }
        }
    }
}
