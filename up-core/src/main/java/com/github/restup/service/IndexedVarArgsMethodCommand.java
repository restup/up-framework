package com.github.restup.service;

import java.lang.reflect.Method;

/**
 * Determines the index mapping of method parameters to the
 * array of available arguments when instantiated
 */
public class IndexedVarArgsMethodCommand extends AbstractIndexedVarArgsMethodCommand {

    private final Integer[] indexes;

    public IndexedVarArgsMethodCommand(Object objectInstance, Method method, Object[] arguments) {
        super(objectInstance, method);
        this.indexes = bind(method, arguments);
    }

    @Override
    protected Integer[] getIndexes(Method method, Object[] args) {
        return indexes;
    }

}
