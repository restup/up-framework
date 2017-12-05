package com.github.restup.service;

/**
 * Defines a var arg command
 *
 * @author abuttaro
 */
public interface MethodCommand<T> {

    T execute(Object... args);

}
