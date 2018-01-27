package com.github.restup.errors;

import com.github.restup.util.Assert;

/**
 * Default {@link ParameterError} implementation
 */
class BasicParameterError implements ParameterError {

    private final String parameterName;
    private final Object parameterValue;

    BasicParameterError(String parameterName, Object parameterValue) {
        Assert.notNull(parameterName, "parameterName is required");
        this.parameterName = parameterName;
        this.parameterValue = parameterValue;
    }

    @Override
    public String getParameterName() {
        return parameterName;
    }

    @Override
    public Object getParameterValue() {
        return parameterValue;
    }

    @Override
    public String getSource() {
        return getParameterName();
    }

}
