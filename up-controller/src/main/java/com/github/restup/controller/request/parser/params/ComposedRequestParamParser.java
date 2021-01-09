package com.github.restup.controller.request.parser.params;

import static com.github.restup.controller.request.parser.params.ParameterMatcher.eq;
import static com.github.restup.controller.request.parser.params.ParameterMatcher.startsWith;
import static com.github.restup.controller.request.parser.params.ParameterParser.ParameterParsers.Unparsed;
import static com.github.restup.controller.request.parser.params.ParameterParserFactory.ParameterParserFactories.FilterNameParameterParserFactory;
import static com.github.restup.controller.request.parser.params.ParameterValueParser.ParameterValueParsers.PAGE_LIMIT;
import static com.github.restup.controller.request.parser.params.ParameterValueParser.ParameterValueParsers.PAGE_OFFSET;

import com.fasterxml.jackson.databind.util.ArrayIterator;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.request.parser.RequestParamParser;
import com.github.restup.controller.request.parser.params.ParameterCountHandler.ParameterCountHandlers;
import com.github.restup.controller.request.parser.params.ParameterParser.ParameterParsers;
import com.github.restup.controller.request.parser.params.ParameterValueErrorHandler.ParameterValueErrorHandlers;
import com.github.restup.controller.request.parser.params.ParameterValueParser.ParameterValueParsers;
import com.github.restup.controller.request.parser.params.ParameterValueScrubber.ParameterValueScrubbers;
import com.github.restup.controller.request.parser.params.ParameterValueValidator.ParameterValueValidators;
import com.github.restup.controller.request.parser.params.ParameterValuesParser.BasicParsedParameterValuesParser;
import java.util.Iterator;
import java.util.function.Function;

public class ComposedRequestParamParser implements RequestParamParser {

    private final ParameterMatcher parameterMatcher;
    private final ParameterParser parameterParser;
    private final ParameterValuesParser parameterValuesParser;

    private ComposedRequestParamParser(
        ParameterMatcher parameterMatcher,
        ParameterParser parameterParser,
        ParameterValuesParser parameterValuesParser) {
        this.parameterMatcher = parameterMatcher;
        this.parameterParser = parameterParser;
        this.parameterValuesParser = parameterValuesParser;
    }

    public static Builder parameter() {
        return new Builder();
    }

    public static Builder parameter(String name) {
        return parameter().equals(name);
    }

    public static Builder parameter(String name, ParameterParser parameterParser) {
        Builder builder = parameter().parseNameWith(parameterParser);
        if (parameterParser == ParameterParsers.Bracketed) {
            return builder.matches(startsWith(name + "["));
        } else if (parameterParser == ParameterParsers.SnakeCase) {
            return builder.matches(startsWith(name + "_"));
        }
        return builder.equals(name);
    }

    public static Builder offset() {
        return offset(OFFSET);
    }

    public static Builder offset(String name) {
        return offset(name, ParameterParsers.Unparsed);
    }

    public static Builder offset(ParameterParser parameterParser) {
        return offset(OFFSET, parameterParser);
    }

    public static Builder offset(String name, ParameterParser parameterParser) {
        return parameter(name, parameterParser)
            .parseValueWith(PAGE_OFFSET)
            .allowedParameterOccurrences(1)
            .trimValues();
    }

    public static Builder limit() {
        return limit(LIMIT);
    }

    public static Builder limit(String name) {
        return limit(name, ParameterParsers.Unparsed);
    }

    public static Builder limit(ParameterParser parameterParser) {
        return limit(LIMIT, parameterParser);
    }

    public static Builder limit(String name, ParameterParser parameterParser) {
        return parameter(name, parameterParser)
            .parseValueWith(PAGE_LIMIT)
            .allowedParameterOccurrences(1)
            .trimValues();
    }

    public static Builder pageNumber() {
        return pageNumber(PAGE_NUMBER);
    }

    public static Builder pageNumber(String name) {
        return pageNumber(name, ParameterParsers.Unparsed);
    }

    public static Builder pageNumber(ParameterParser parameterParser) {
        return pageNumber(PAGE_NUMBER, parameterParser);
    }

    public static Builder pageNumber(String name, ParameterParser parameterParser) {
        return parameter(name, parameterParser)
            .parseValueWith(ParameterValueParsers.PAGE_NUMBER)
            .allowedParameterOccurrences(1)
            .trimValues();
    }

    public static Builder sort() {
        return sort(SORT);
    }

    public static Builder sort(String name) {
        return sort(name, ParameterParsers.Unparsed);
    }

    public static Builder sort(ParameterParser parameterParser) {
        return sort(SORT, parameterParser);
    }

    public static Builder sort(String name, ParameterParser parameterParser) {
        return parameter(name, parameterParser)
            .parseValueWith(ParameterValueParsers.SORT)
            .delimitedValues(",")
            .ignoreBlankValues()
            .trimValues();
    }

    public static Builder fields() {
        return fields(FIELDS);
    }

    public static Builder fields(String name) {
        return fields(name, ParameterParsers.Unparsed);
    }

    public static Builder fields(ParameterParser parameterParser) {
        return fields(FIELDS, parameterParser);
    }

    public static Builder fields(String name, ParameterParser parameterParser) {
        return parameter(name, parameterParser)
            .parseValueWith(ParameterValueParsers.FIELDS)
            .delimitedValues(",")
            .ignoreBlankValues()
            .trimValues();
    }

    public static Builder filter() {
        return filter(FILTER);
    }

    public static Builder filter(String name) {
        return filter(name, ParameterParsers.Bracketed);
    }

    public static Builder filter(ParameterParser parameterParser) {
        return filter(FILTER, parameterParser);
    }

    public static Builder filter(String name, ParameterParser parameterParser) {
        return parameter(name, parameterParser)
            .parseNameWith(FilterNameParameterParserFactory.convert(parameterParser))
            .parseValueWith(new FilterParameterValueParser())
            .delimitedValues(",")
            .trimValues();
    }

    public static Builder include() {
        return include(INCLUDE);
    }

    public static Builder include(String name) {
        return include(name, ParameterParsers.Unparsed);
    }

    public static Builder include(ParameterParser nameParser) {
        return include(INCLUDE, nameParser);
    }

    public static Builder include(String name, ParameterParser nameParser) {
        return include(name, nameParser, nameParser);
    }

    public static Builder include(ParameterParser nameParser, ParameterParser valueParser) {
        return include(INCLUDE, nameParser, valueParser);
    }

    public static Builder include(String name, ParameterParser nameParser,
        ParameterParser valueParser) {
        return parameter(name, nameParser)
            .parseValueWith(IncludeParameterValueParser.forParser(valueParser))
            .delimitedValues(",")
            .ignoreBlankValues()
            .trimValues();
    }

    @Override
    public boolean accept(String parameterName) {
        return parameterMatcher.accept(parameterName);
    }

    @Override
    public <T> void parse(ResourceControllerRequest request,
        ParsedResourceControllerRequest.Builder<T> builder,
        String parameterName, String... parameterValues) {
        parse(ParameterParsingContext.of(request, builder, parameterName, parameterValues));
    }

    void parse(ParameterParsingContext ctx) {

        ParameterParserResult parameterParserResult = parameterParser
            .parse(ctx, ctx.getRawParameterName());

        if (parameterParserResult.getResource() != null) {
            parameterValuesParser.parse(ctx, parameterParserResult);
        }
    }

    public static class Builder {


        private Function<String[], Iterator> parameterIterator;
        private ParameterMatcher parameterMatcher;
        private ParameterParser parameterParser;


        private ParameterValueParser parameterValueParser;
        private ParameterValueScrubber parameterValueScrubber;
        private ParameterValueValidator parameterValueValidator;
        private ParameterValueErrorHandler parameterValueErrorHandler;
        private ParameterCountHandler parameterCountHandler;

        private Builder() {

        }

        private Builder me() {
            return this;
        }

        public Builder equals(String s) {
            return matches(eq(s));
        }

        public Builder matches(ParameterMatcher matcher) {
            parameterMatcher = matcher;
            return me();
        }

        public Builder parseNameWith(ParameterParser parameterParser) {
            this.parameterParser = parameterParser;
            return me();
        }

        public Builder parseValueWith(ParameterValueParser parameterValueParser) {
            this.parameterValueParser = parameterValueParser;
            return me();
        }

        public Builder delimitedValues(String delimiter) {
            return parameterIterator((arr) -> new DelimitedParameterIterator(delimiter, arr));
        }

        public Builder ignoreEmptyValues() {
            return ignoreEmptyValues(true);
        }

        public Builder ignoreEmptyValues(boolean ignoreEmptyValues) {
            if (ignoreEmptyValues) {
                parameterValueErrorHandler(ParameterValueErrorHandlers.IGNORE_EMPTY);
            }
            return parameterValueValidator(ParameterValueValidators.IS_NOT_EMPTY);
        }

        public Builder ignoreBlankValues() {
            return ignoreBlankValues(true);
        }

        public Builder ignoreBlankValues(boolean ignoreBlankValues) {
            if (ignoreBlankValues) {
                parameterValueErrorHandler(ParameterValueErrorHandlers.IGNORE_BLANK);
            }
            return parameterValueValidator(ParameterValueValidators.IS_NOT_EMPTY);
        }

        public Builder allowMultipleValues(boolean allowMultiple) {
            return allowMultiple ? allowedParameterOccurrences(0) : allowedParameterOccurrences(1);
        }

        /*
         * The number of occurrences of the parameter permitted. < 1 is unlimited
         */
        public Builder allowedParameterOccurrences(int allowedParameterOccurrences) {
            return parameterCountHandler(ParameterCountHandler.of(allowedParameterOccurrences));
        }

        public Builder trimValues() {
            return trimValues(true);
        }

        public Builder trimValues(boolean ignoreBlankValues) {
            return ignoreBlankValues ? parameterValueScrubber(ParameterValueScrubbers.TRIMMED)
                : parameterValueScrubber(ParameterValueScrubbers.NOOP);
        }

        public Builder parameterValueScrubber(ParameterValueScrubber parameterValueScrubber) {
            this.parameterValueScrubber = parameterValueScrubber;
            return me();
        }

        public Builder parameterValueValidator(ParameterValueValidator parameterValueValidator) {
            this.parameterValueValidator = parameterValueValidator;
            return me();
        }

        public Builder parameterValueErrorHandler(
            ParameterValueErrorHandler parameterValueErrorHandler) {
            this.parameterValueErrorHandler = parameterValueErrorHandler;
            return me();
        }

        public Builder parameterCountHandler(ParameterCountHandler parameterCountHandler) {
            this.parameterCountHandler = parameterCountHandler;
            return me();
        }

        public Builder parameterIterator(Function<String[], Iterator> parameterIterator) {
            this.parameterIterator = parameterIterator;
            return me();
        }

        public ComposedRequestParamParser build() {
            if (parameterMatcher == null) {
                throw new IllegalStateException("parameterMatcher is required");
            }
            if (parameterValueParser == null) {
                throw new IllegalStateException("parameterValueParser is required");
            }

            ParameterMatcher matcher = parameterMatcher;
            ParameterParser nameParser = parameterParser;

            if (nameParser == null) {
                nameParser = Unparsed;
            }

            ParameterValueParser pvParser = parameterValueParser;
            ParameterValueScrubber pvScrubber = parameterValueScrubber;
            if (pvScrubber == null) {
                pvScrubber = ParameterValueScrubbers.NOOP;
            }
            ParameterValueValidator pvValidator = parameterValueValidator;
            if (pvValidator == null) {
                pvValidator = ParameterValueValidators.NOOP;
            }
            ParameterValueErrorHandler pvErrorHandler = parameterValueErrorHandler;
            if (pvErrorHandler == null) {
                pvErrorHandler = ParameterValueErrorHandlers.DEFAULT;
            }
            ParameterCountHandler pcHandler = parameterCountHandler;
            if (pcHandler == null) {
                pcHandler = ParameterCountHandlers.NOOP;
            }

            Function<String[], Iterator> pIterator = parameterIterator;
            if (pIterator == null) {
                pIterator = (arr) -> new ArrayIterator(arr);
            }

            ParameterValuesParser valuesParser = BasicParsedParameterValuesParser
                .of(pIterator, pvParser, pvScrubber, pvValidator, pvErrorHandler,
                    pcHandler);

            return new ComposedRequestParamParser(matcher, nameParser, valuesParser);
        }
    }

}
