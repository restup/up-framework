package com.github.restup.bind.converter;

import java.util.function.Function;
import com.github.restup.errors.Errors;

/**
 * Interface for converting a parameter from input type to the appropriate field type
 * 
 * @param <T> from type
 * @param <R> to type
 */
public interface ParameterConverter<T,R> extends Function<T, R> {

    /**
     * Convert a parameter, appending any errors to provided errors object
     *
     * @param parameterName name of the parameter
     * @param from value of the parameter requiring conversion
     * @param errors to append any conversion errors to
     * @return the resulting value
     */
    R convert(String parameterName, T from, Errors errors);

}
