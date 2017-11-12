package com.github.restup.bind.converter;

import com.github.restup.errors.ErrorFactory;

public class StringToIntegerConverter extends StringConverter<Integer> {

    protected StringToIntegerConverter(ErrorFactory errorFactory) {
        super(errorFactory, Integer.class, Integer.TYPE);
    }

    @Override
    Integer convertValue(String from) {
        return Integer.valueOf(from);
    }

}
