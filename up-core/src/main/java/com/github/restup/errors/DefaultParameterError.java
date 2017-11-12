package com.github.restup.errors;

import com.github.restup.util.Assert;

/**
 * Default {@link ParameterError} implementation
 */
class DefaultParameterError implements ParameterError {

    private final String parameterName;
    private final Object parameterValue;

    DefaultParameterError(String parameterName, Object parameterValue) {
        Assert.notNull(parameterName, "parameterName is required");
        this.parameterName = parameterName;
        this.parameterValue = parameterValue;
    }

    public String getParameterName() {
        return parameterName;
    }

    public Object getParameterValue() {
        return parameterValue;
    }

    public String getSource() {
        return getParameterName();
    }

}
