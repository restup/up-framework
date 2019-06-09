package com.github.restup.controller.request.parser.params;

import com.github.restup.errors.ErrorCode;
import com.github.restup.errors.RequestError;
import com.github.restup.registry.Resource;

public interface ParameterResourceParser {

    /**
     * @param tokens parsed tokens from the  parameter name
     */
    Resource parseResource(ParameterParsingContext ctx, String... tokens);

    Resource parseResourceAt(ParameterParsingContext ctx, int index, String... tokens);

    enum ParameterResourceParsers implements ParameterResourceParser {
        /**
         * Returns the request resource always
         */
        Requested {
            @Override
            public Resource parseResource(ParameterParsingContext ctx, String... tokens) {
                return ctx.getResource();
            }

            @Override
            public Resource parseResourceAt(ParameterParsingContext ctx, int index,
                String... tokens) {
                return ctx.getResource();
            }
        },
        /**
         * Returns a valid resource name from a parsed parameter name (ex. fields[resource]) and the
         * request resource if not
         */
        Parsed {
            @Override
            public Resource parseResource(ParameterParsingContext ctx, String... tokens) {
                Resource resource = null;
                for (int i = tokens.length - 1; i >= 0; i--) {
                    String resourceName = tokens[i];
                    resource = ctx.getResource(resourceName);
                    if (resource != null) {
                        break;
                    }
                }
                return errorIfNull(ctx, resource);
            }

            @Override
            public Resource parseResourceAt(ParameterParsingContext ctx, int index,
                String... tokens) {
                Resource resource = null;
                if (tokens.length > index) {
                    String resourceName = tokens[index];
                    resource = ctx.getResource(resourceName);
                }
                return errorIfNull(ctx, resource);
            }

            private Resource errorIfNull(ParameterParsingContext ctx, Resource resource) {
                if (resource == null) {
                    RequestError.Builder error = ParameterParsingContext
                        .parameterError(ctx, ctx.getRawParameterValues())
                        .code(ErrorCode.PARAMETER_INVALID_RESOURCE)
                        .resource(ctx.getResource())
                        .detail("Parameter ''{0}'' does not specify a valid resource",
                            ctx.getRawParameterName());
                    ctx.addError(error);
                }
                return resource;
            }
        }
    }

}
