package com.github.restup.controller.request.parser;


import com.github.restup.config.ConfigurationContext;
import com.github.restup.controller.request.parser.RequestParser.Builder;

@FunctionalInterface
public interface RequestParserBuilderDecorator {

    /**
     * Decorate a {@link Builder}.  Useful in dependency injection frameworks such as Spring where a
     * default builder can complete basic wiring requiring only customization to be configured.
     *
     * @param builder to decorate
     * @return the builder
     */
    Builder decorate(ConfigurationContext configurationContext, Builder builder);

}
