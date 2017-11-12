package com.github.restup.bind.converter;

import com.github.restup.errors.ErrorFactory;

public class StringToLongConverter extends StringConverter<Long> {

    protected StringToLongConverter(ErrorFactory errorFactory) {
        super(errorFactory, Long.class, Long.TYPE);
    }

    @Override
    Long convertValue(String from) {
        return Long.valueOf(from);
    }

}
