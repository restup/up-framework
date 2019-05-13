package com.github.restup.mapping.fields;

import com.github.restup.mapping.fields.MappedFieldBuilderVisitor.Builder;

@FunctionalInterface
public interface MappedFieldBuilderVisitorBuilderDecorator {


    /**
     * Decorate a {@link Builder}.  Useful in dependency injection frameworks such as Spring where a
     * default builder can complete basic wiring requiring only customization to be configured.
     *
     * @param builder to decorate
     * @return the builder
     */
    Builder decorate(Builder builder);

}
