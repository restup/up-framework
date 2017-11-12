package com.github.restup.bind;

import com.github.restup.errors.Errors;
import com.github.restup.service.FilterChainContext;
import com.github.restup.util.ReflectionUtils;

/**
 * Creates a new instance of method arguments using reflection.
 * <p>
 * Does not bind request parameters to instantiated objects.
 */
public class SimpleMethodArgumentFactory implements MethodArgumentFactory {

    public <T> T newInstance(Class<T> clazz) {
        return ReflectionUtils.newInstance(clazz);
    }

    public <T> T newInstance(Class<T> clazz, FilterChainContext ctx, Errors errors) {
        return newInstance(clazz);
    }

}
