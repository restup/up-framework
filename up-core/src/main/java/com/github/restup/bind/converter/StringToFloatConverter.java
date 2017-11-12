package com.github.restup.bind.converter;

import com.github.restup.errors.ErrorFactory;

public class StringToFloatConverter extends StringConverter<Float> {

    protected StringToFloatConverter(ErrorFactory errorFactory) {
        super(errorFactory, Float.class, Float.TYPE);
    }

    @Override
    Float convertValue(String from) {
        return Float.valueOf(from);
    }

}
