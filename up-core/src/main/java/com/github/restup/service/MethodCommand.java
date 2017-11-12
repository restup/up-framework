package com.github.restup.service;

/**
 * Defines a var arg command
 *
 * @param <T>
 * @author abuttaro
 */
public interface MethodCommand<T> {

    T execute(Object... args);

}
