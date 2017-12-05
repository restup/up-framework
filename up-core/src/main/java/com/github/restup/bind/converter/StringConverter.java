package com.github.restup.bind.converter;

import com.github.restup.errors.ErrorBuilder;
import com.github.restup.errors.ErrorFactory;
import com.github.restup.errors.Errors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class StringConverter<T> implements ParameterConverter<String, T> {

    private final static Logger log = LoggerFactory.getLogger(StringConverter.class);

    private final Class<?>[] convertsTo;

    private final ErrorFactory errorFactory;

    protected StringConverter(ErrorFactory errorFactory, Class<?>... clazz) {
        this.convertsTo = clazz;
        this.errorFactory = errorFactory;
    }

    abstract T convertValue(String from);

    protected T convertValue(String parameterName, String from, Errors errors) {
        return convertValue(from);
    }

    public final T convert(String parameterName, String from, Errors errors) {
        try {
            return convertValue(parameterName, from, errors);
        } catch (Exception e) {
            log.debug("Unable to convert parameter " + parameterName + "=" + from, e);
            errors.addError(
                    ErrorBuilder.builder()
                            .code("PARAMETER_CONVERSION")
                            .title("Conversion Error")
                            .detail("Unable to convert value to correct type")
                            .source(getErrorFactory().createParameterError(parameterName, from))
                            .meta(parameterName, from));
        }
        return null;
    }

    public Class<?>[] getConvertsFrom() {
        return new Class[]{String.class};
    }

    public Class<?>[] getConvertsTo() {
        return convertsTo;
    }

    public ErrorFactory getErrorFactory() {
        return errorFactory;
    }
}
