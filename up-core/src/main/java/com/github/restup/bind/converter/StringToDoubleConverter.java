package com.github.restup.bind.converter;

import com.github.restup.errors.ErrorFactory;

public class StringToDoubleConverter extends StringConverter<Double> {

    protected StringToDoubleConverter(ErrorFactory errorFactory) {
        super(errorFactory, Double.class, Double.TYPE);
    }

    @Override
    Double convertValue(String from) {
        return Double.valueOf(from);
    }

}
