package com.github.restup.bind.converter;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.restup.errors.ErrorBuilder;
import com.github.restup.errors.ErrorFactory;
import com.github.restup.errors.Errors;

public class FunctionalParameterConverter<T> implements ParameterConverter<String, T> {

    private final static Logger log = LoggerFactory.getLogger(FunctionalParameterConverter.class);

    private final ErrorFactory errorFactory;
    
    private final Function<String,T> function;

    protected FunctionalParameterConverter(Function<String,T> function, ErrorFactory errorFactory) {
        this.errorFactory = errorFactory;
        this.function = function;
    }

	@Override
	public final T apply(String from) {
        return function.apply(from);
	}

    protected T convertValue(String parameterName, String from, Errors errors) {
        return apply(from);
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

    public ErrorFactory getErrorFactory() {
        return errorFactory;
    }
}
