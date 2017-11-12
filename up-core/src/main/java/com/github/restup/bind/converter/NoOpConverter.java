package com.github.restup.bind.converter;

import com.github.restup.errors.Errors;

@SuppressWarnings("rawtypes")
public class NoOpConverter implements ParameterConverter {

    public Object convert(String parameterName, Object from, Errors errors) {
        return from;
    }

    public Class<?>[] getConvertsFrom() {
        return new Class[]{Object.class};
    }

    public Class<?>[] getConvertsTo() {
        return new Class[]{Object.class};
    }
}
