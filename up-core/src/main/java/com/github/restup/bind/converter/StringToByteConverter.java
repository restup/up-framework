package com.github.restup.bind.converter;

import com.github.restup.errors.ErrorFactory;

public class StringToByteConverter extends StringConverter<Byte> {

    protected StringToByteConverter(ErrorFactory errorFactory) {
        super(errorFactory, Byte.class, Byte.TYPE);
    }

    @Override
    Byte convertValue(String from) {
        return Byte.valueOf(from);
    }

}
