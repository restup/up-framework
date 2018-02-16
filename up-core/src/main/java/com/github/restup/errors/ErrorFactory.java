package com.github.restup.errors;

/**
 * A factory to obtain error object instances.
 *
 * @author andy.buttaro
 */
public interface ErrorFactory {

    static ErrorFactory getDefaultErrorFactory() {
        return DefaultErrorFactory.getInstance();
    }

    /**
     * @return A new {@link Errors} instance
     */
    Errors createErrors();

    /**
     * @param parameterName name of parameter
     * @param parameterValue value of parameter
     * @return A new {@link ParameterError} instance
     */
    ParameterError createParameterError(String parameterName, Object parameterValue);

}
