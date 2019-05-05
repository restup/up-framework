package com.github.restup.controller.request.parser.params;

import static com.github.restup.controller.request.parser.params.ParameterResourceParser.ParameterResourceParsers.Requested;

@FunctionalInterface
public interface ParameterParser {

    ParameterParserResult parse(ParameterParsingContext ctx, String value);

    enum ParameterParsers implements ParameterParser {
        /**
         * No parameter parsing.  Resource is set to the request resource
         */
        Unparsed {
            @Override
            public ParameterParserResult parse(ParameterParsingContext ctx, String value) {
                return ParameterParserResult.of(Requested.parseResource(ctx, value));
            }
        },
        /**
         * Bracketed parameter parsing.
         */
        Bracketed {
            private final BracketedParameterParser parser = new BracketedParameterParser();

            @Override
            public ParameterParserResult parse(ParameterParsingContext ctx, String value) {
                return parser.parse(ctx, value);
            }
        },
        SnakeCase {
            @Override
            public ParameterParserResult parse(ParameterParsingContext ctx,
                String value) {
                // some complexities here. can't just split on _
                // sort_resource_name
                // resource_name_field_name=foo
                return null;
            }
        }
    }
}
