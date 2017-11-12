package com.github.restup.bind.converter;

import com.github.restup.errors.Errors;

/**
 * Interface for converting a parameter from input type to the appropriate field type
 *
 * @param <F>
 * @param <T>
 */
public interface ParameterConverter<F, T> {

    /**
     * Convert a parameter, appending any errors to provided errors object
     *
     * @param parameterName name of the parameter
     * @param from          value of the parameter requiring conversion
     * @param errors        to append any conversion errors to
     * @return
     */
    T convert(String parameterName, F from, Errors errors);

    /**
     * @return the types the implementation supports conversion from.  For binding, should always be String
     */
    Class<?>[] getConvertsFrom();

    /**
     * @return the types the implementation supports conversion to.  Typically expected to be 1 type, but in the case of
     * primitives and their wrappers expected to be 2
     */
    Class<?>[] getConvertsTo();

}
