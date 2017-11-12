package com.github.restup.bind.converter;

import com.github.restup.errors.ErrorFactory;

public class StringToCharConverter extends StringConverter<Character> {

    protected StringToCharConverter(ErrorFactory errorFactory) {
        super(errorFactory, Character.class, Character.TYPE);
    }

    @Override
    Character convertValue(String from) {
        //TODO could make this more robust with better errors detail
        return from.charAt(0);
    }

}
