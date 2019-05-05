package com.github.restup.controller.request.parser.params;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@FunctionalInterface
public interface ParameterValueErrorHandler {

    void handle(ParameterParsingContext ctx, String rawValue);

    enum ParameterValueErrorHandlers implements ParameterValueErrorHandler {
        NOOP {
            @Override
            public void handle(ParameterParsingContext ctx, String rawValue) {

            }
        },
        DEFAULT {
            @Override
            public void handle(ParameterParsingContext ctx, String rawValue) {
                ctx.addParameterError(rawValue);
            }
        },
        IGNORE_EMPTY {
            @Override
            public void handle(ParameterParsingContext ctx, String rawValue) {
                if (isNotEmpty(rawValue)) {
                    DEFAULT.handle(ctx, rawValue);
                }
            }
        },
        IGNORE_BLANK {
            @Override
            public void handle(ParameterParsingContext ctx, String rawValue) {
                if (isNotBlank(rawValue)) {
                    DEFAULT.handle(ctx, rawValue);
                }
            }
        }
    }

}
