package com.github.restup.bind.converter;

import com.github.restup.errors.ErrorFactory;

import java.time.LocalTime;

public class StringToLocalTimeConverter extends StringConverter<LocalTime> {

    public StringToLocalTimeConverter(ErrorFactory errorFactory) {
        super(errorFactory, LocalTime.class);
    }

    @Override
    LocalTime convertValue(String s) {
        if (s.charAt(2) != ':') {
            // pad hour with 0 if needed
            return LocalTime.parse("0" + s);
        } else {
            return LocalTime.parse(s);
        }
    }

}
