package com.github.restup.annotations.model;

@FunctionalInterface
public interface StatusCodeProvider {

    StatusCode getStatusCode();

}
