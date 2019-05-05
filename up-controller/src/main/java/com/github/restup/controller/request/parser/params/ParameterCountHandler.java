package com.github.restup.controller.request.parser.params;

import com.github.restup.errors.RequestError;
import java.util.Arrays;

@FunctionalInterface
public interface ParameterCountHandler {

    static ParameterCountHandler of(int count) {
        if (count < 1) {
            return ParameterCountHandlers.NOOP;
        }
        if (count == 1) {
            return ParameterCountHandlers.SINGLE;
        }
        return new MaxParameterCountHandler(count);
    }

    boolean accept(ParameterParsingContext ctx, int count);

    enum ParameterCountHandlers implements ParameterCountHandler {
        NOOP {
            @Override
            public boolean accept(ParameterParsingContext ctx, int count) {
                return true;
            }
        },
        SINGLE {
            @Override
            public boolean accept(ParameterParsingContext ctx, int count) {
                if (count != 1) {
                    RequestError.Builder error = RequestError.builder()
                        .code("INVALID_PARAMETER_OCCURRENCES")
                        .title("Parameter may only occur once per request")
                        .detail("''{0}'' expected once and received {1} times",
                            ctx.getRawParameterName(), count);
                    ctx.getBuilder().addParameterError(error, ctx.getRawParameterName(),
                        ctx.getRawParameterValues());
                    return false;
                }
                return true;
            }
        }
    }

    class MaxParameterCountHandler implements ParameterCountHandler {

        final int max;

        public MaxParameterCountHandler(int max) {
            this.max = max;
        }

        @Override
        public boolean accept(ParameterParsingContext ctx, int count) {
            if (count > max) {
                RequestError.Builder error = RequestError.builder()
                    .code("MAX_PARAMETER_OCCURRENCES")
                    .title("Parameter occurs too many times in request")
                    .detail("Maximum number of values  occurrences {0} occurs {1} times",
                        ctx.getRawParameterName(), count, max);
                ctx.getBuilder().addParameterError(error, ctx.getRawParameterName(),
                    Arrays.asList(ctx.getRawParameterValues()));
                return false;
            }
            return true;
        }
    }

}
