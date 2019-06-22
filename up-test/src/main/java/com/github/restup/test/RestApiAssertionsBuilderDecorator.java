package com.github.restup.test;

@FunctionalInterface
public interface RestApiAssertionsBuilderDecorator {

    RestApiAssertions.Builder decorate(RestApiAssertions.Builder builder);

}
