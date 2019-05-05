package com.github.restup.controller.request.parser.params;

import java.util.Iterator;
import java.util.function.Function;

@FunctionalInterface
public interface ParameterValuesParser {

    void parse(ParameterParsingContext ctx, ParameterParserResult parsedParameerNameResult);

    /**
     * Basic parser that iterates over parameterValues using a {@link ParameterValueParser} to parse
     * each parameter value separately.
     */
    final class BasicParsedParameterValuesParser implements ParameterValuesParser {

        final Function<String[], Iterator> parameterIterator;
        final ParameterValueParser parser;
        final ParameterValueScrubber valueScrubber;
        final ParameterValueValidator valueValidator;
        final ParameterValueErrorHandler errorHandler;
        final ParameterCountHandler countHandler;

        private BasicParsedParameterValuesParser(
            Function<String[], Iterator> parameterIterator,
            ParameterValueParser parser,
            ParameterValueScrubber valueScrubber, ParameterValueValidator valueValidator,
            ParameterValueErrorHandler errorHandler, ParameterCountHandler countHandler) {
            this.parameterIterator = parameterIterator;
            this.parser = parser;
            this.valueScrubber = valueScrubber;
            this.valueValidator = valueValidator;
            this.errorHandler = errorHandler;
            this.countHandler = countHandler;
        }

        static BasicParsedParameterValuesParser of(
            Function<String[], Iterator> parameterIterator, ParameterValueParser parser,
            ParameterValueScrubber valueScrubber, ParameterValueValidator valueValidator,
            ParameterValueErrorHandler errorHandler, ParameterCountHandler countHandler) {
            return new BasicParsedParameterValuesParser(parameterIterator, parser, valueScrubber,
                valueValidator,
                errorHandler, countHandler);
        }

        @Override
        public void parse(ParameterParsingContext ctx,
            ParameterParserResult parsedParameterNameResult) {

            int count = 0;
            Iterator<String> it = parameterIterator.apply(ctx.getRawParameterValues());
            while (it.hasNext()) {
                String rawValue = it.next();
                String scrubbed = valueScrubber.scrub(rawValue);
                if (valueValidator.isValid(scrubbed)) {
                    if (countHandler.accept(ctx, ++count)) {
                        parser.parse(ctx, parsedParameterNameResult, rawValue, scrubbed);
                    } else {
                        // no need to continue if additional are not supported and
                        // presumably an error is already added by accept
                        return;
                    }
                } else {
                    errorHandler.handle(ctx, rawValue);
                }
            }
            countHandler.accept(ctx, count);
        }
    }
}
