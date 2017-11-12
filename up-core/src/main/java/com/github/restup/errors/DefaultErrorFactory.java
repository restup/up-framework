package com.github.restup.errors;

/**
 * Default {@link ErrorFactory} implementation
 */
public final class DefaultErrorFactory extends ErrorFactory {

    public Errors createErrors() {
        return new DefaultErrors();
    }

    public ParameterError createParameterError(String parameterName, Object parameterValue) {
        return new DefaultParameterError(parameterName, parameterValue);
    }

}
