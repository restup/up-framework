package com.github.restup.registry;

import com.github.restup.registry.ResourceRegistry.Builder;

@FunctionalInterface
public interface ResourceRegistryBuilderDecorator {

    /**
     * Decorate a {@link Builder}.  Useful in dependency injection frameworks such as Spring where a
     * default builder can complete basic wiring requiring only customization to be configured.
     *
     * @param builder to decorate
     * @return the builder
     */
    Builder decorate(Builder builder);

}
