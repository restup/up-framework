package com.github.restup.test;

@FunctionalInterface
public interface ApiResponseFilter<T> {

    boolean accept(ApiResponse<T> response);

}
