package com.github.restup.errors;

/**
 * {@link ErrorSource} describing a parameter error
 */
public interface ParameterError extends ErrorSource {

    /**
     * @return The name of the parameter in error.
     */
    String getParameterName();

    /**
     * @return The value of the parameter in error
     */
    public Object getParameterValue();

}
