package com.github.restup.test;

@FunctionalInterface
public interface RpcApiAssertionsBuilderDecorator {

    RpcApiAssertions.Builder decorate(RpcApiAssertions.Builder builder);

}
