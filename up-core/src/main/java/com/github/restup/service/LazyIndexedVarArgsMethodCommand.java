package com.github.restup.service;

import java.lang.reflect.Method;

/**
 * Determines the index mapping of method parameters to the array of available arguments on first execution of the method, lazily as needed.
 */
public class LazyIndexedVarArgsMethodCommand extends AbstractIndexedVarArgsMethodCommand {

    Integer[] indexes;

    public LazyIndexedVarArgsMethodCommand(Object objectInstance, Method method) {
        super(objectInstance, method);
    }

    @Override
    protected Integer[] getIndexes(Method method, Object[] args) {
        if (indexes == null) {
            indexes = bind(method, args);
        }
        return indexes;
    }

}
