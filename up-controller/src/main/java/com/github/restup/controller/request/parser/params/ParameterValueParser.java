package com.github.restup.controller.request.parser.params;

import com.github.restup.query.ResourceQueryStatement.Type;
import com.github.restup.query.ResourceSort;

@FunctionalInterface
public interface ParameterValueParser<T> {

    void parse(ParameterParsingContext ctx, ParameterParserResult parsedParameterNameResult,
        String rawValue,
        T parameterValue);


    enum ParameterValueParsers implements ParameterValueParser<String> {

        /**
         * Parses fields parameters. <p> Ex: For a resource foo with fields named a, b, &amp; c
         * </p>
         *
         * <pre>
         * ?fields=a,b
         * ?filter[foo]=a,b
         * </pre>
         */
        FIELDS {
            @Override
            public void parse(ParameterParsingContext ctx,
                ParameterParserResult result, String rawValue,
                String value) {

                if (value.equals("*")) {
                    ctx.getBuilder()
                        .setFieldRequest(result.getResource(), Type.All);
                } else if (value.equals("**")) {
                    ctx.getBuilder()
                        .setFieldRequest(result.getResource(), Type.Every);
                } else if (value.startsWith("+")) {
                    ctx.getBuilder().addAdditionalField(result.getResource(), value.substring(1));
                } else if (value.startsWith("-")) {
                    ctx.getBuilder().addExcludedField(result.getResource(), value.substring(1));
                } else {
                    ctx.getBuilder().addRequestedField(result.getResource(), value);
                }
            }
        },

        /**
         * Parses an integer limit for number of records returned by pagination. <p> <p> Ex: <p>
         * <pre>
         * ?limit=100
         * </pre>
         */
        PAGE_LIMIT {
            private final NumberParameterValueParser impl = new NumberParameterValueParser(
                (ctx, result, rawValue, value) -> {
                    ctx.getBuilder().setPageLimit(ctx.getRawParameterName(), value);
                });

            @Override
            public void parse(ParameterParsingContext ctx,
                ParameterParserResult parsedParameterNameResult,
                String rawValue, String parameterValue) {
                impl.parse(ctx, parsedParameterNameResult, rawValue, parameterValue);
            }
        },
        /**
         * <p> Parses an integer offset for pagination. Named offset by default </p> Ex:
         *
         * <pre>
         * ?offset=0
         * </pre>
         */
        PAGE_OFFSET {
            private final NumberParameterValueParser impl = new NumberParameterValueParser(
                (ctx, result, rawValue, value) -> {
                    ctx.getBuilder().setPageOffset(ctx.getRawParameterName(), value);
                });

            @Override
            public void parse(ParameterParsingContext ctx,
                ParameterParserResult parsedParameterNameResult,
                String rawValue, String parameterValue) {
                impl.parse(ctx, parsedParameterNameResult, rawValue, parameterValue);
            }
        },
        /**
         * <p> Similar to offset, but pageNumber is 1 based, so the value has to be adjusted </p>
         * <p> Ex: </p>
         *
         * <pre>
         * ?pageNumber=1
         * </pre>
         */
        PAGE_NUMBER {
            private final NumberParameterValueParser impl = new NumberParameterValueParser(
                (ctx, result, rawValue, value) -> {
                    //TODO support pageNumber[resource] for paginated query
                    ctx.getBuilder()
                        .setPageOffset(ctx.getRawParameterName(), adjusted(value), true);
                });

            private Integer adjusted(Integer value) {
                if (value != null && value > 0) {
                    return value - 1;
                }
                return value;
            }

            @Override
            public void parse(ParameterParsingContext ctx,
                ParameterParserResult parsedParameterNameResult,
                String rawValue, String parameterValue) {
                impl.parse(ctx, parsedParameterNameResult, rawValue, parameterValue);
            }
        },
        /**
         * <p> Parses a parameter of delimited field names specifying sort order. ',' delimited and
         * named sort by default. By default sort order is ascending, but can be changed by using
         * '-' prefix ahead of a field name </p> <p> Ex: For a resource with fields named foo &amp;
         * bar </p>
         *
         * <pre>
         * ?sort=foo,bar
         * ?sort=+foo,-bar
         * </pre>
         */
        SORT {
            @Override
            public void parse(ParameterParsingContext ctx,
                ParameterParserResult parsedParameterNameResult,
                String rawValue, String parameterValue) {

                Boolean asc = ResourceSort.isAscending(parameterValue.charAt(0));
                // parse out +/- if necessary
                String field = asc == null ? parameterValue : parameterValue.substring(1);
                // add parameter
                ctx.getBuilder().addSort(ctx.getRawParameterName(), rawValue, field, asc);
            }
        }

    }

    class NumberParameterValueParser implements ParameterValueParser<String> {

        private final ParameterValueParser<Integer> delegate;

        public NumberParameterValueParser(
            ParameterValueParser<Integer> delegate) {
            this.delegate = delegate;
        }

        @Override
        public void parse(ParameterParsingContext ctx,
            ParameterParserResult result, String rawValue,
            String value) {
            try {
                delegate.parse(ctx, result, rawValue, Integer.valueOf(value));
            } catch (NumberFormatException ex) {
                ctx.getBuilder().addParameterError(ctx.getRawParameterName(), rawValue);
            }
        }
    }
}
