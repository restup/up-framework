package com.github.restup.bind.converter;

import com.github.restup.errors.ErrorFactory;

public class StringToShortConverter extends StringConverter<Short> {

    protected StringToShortConverter(ErrorFactory errorFactory) {
        super(errorFactory, Short.class, Short.TYPE);
    }

    @Override
    Short convertValue(String from) {
        return Short.valueOf(from);
    }

}
