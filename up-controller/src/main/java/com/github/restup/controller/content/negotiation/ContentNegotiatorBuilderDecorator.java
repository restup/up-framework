package com.github.restup.controller.content.negotiation;

import com.github.restup.controller.content.negotiation.ContentNegotiator.Builder;

@FunctionalInterface
public interface ContentNegotiatorBuilderDecorator {

    /**
     * Decorate a {@link Builder}.  Useful in dependency injection frameworks such as Spring where a
     * default builder can complete basic wiring requiring only customization to be configured.
     *
     * @param builder to decorate
     * @return the builder
     */
    Builder decorate(Builder builder);

}
