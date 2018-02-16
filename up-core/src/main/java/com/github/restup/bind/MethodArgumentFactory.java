package com.github.restup.bind;

import com.github.restup.bind.converter.ParameterConverterFactory;
import com.github.restup.errors.Errors;
import com.github.restup.mapping.MappedClassRegistry;
import com.github.restup.service.FilterChainContext;

/**
 * Factory used to provide pojo instances of filter method arguments
 */
public interface MethodArgumentFactory {


    static MethodArgumentFactory getDefaultInstance(MappedClassRegistry mappedClassRegistry, ParameterConverterFactory parameterConverterFactory) {
        return new DefaultMethodArgumentFactory(mappedClassRegistry, parameterConverterFactory);
    }

    /**
     * Create a new instance of clazz
     *
     * @param clazz the class to instantiate
     * @param <T> the type of the class
     * @return a new object instance
     */
    <T> T newInstance(Class<T> clazz);

    /**
     * Create a new instance of clazz, appending any errors to {@link Errors}
     *
     * @param clazz to instantiate
     * @param ctx the context of the filter execution requiring the argument
     * @param errors to append any errors to
     * @param <T> the type of the class
     * @return a new object instance
     */
    <T> T newInstance(Class<T> clazz, FilterChainContext ctx, Errors errors);

}
