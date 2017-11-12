package com.github.restup.bind.converter;

import com.github.restup.errors.ErrorFactory;

import java.time.LocalDate;

public class StringToLocalDateConverter extends StringConverter<LocalDate> {

    public StringToLocalDateConverter(ErrorFactory errorFactory) {
        super(errorFactory, LocalDate.class);
    }

    @Override
    LocalDate convertValue(String s) {
        if (s.length() > 10) {
            return LocalDate.parse(s.substring(0, 10));
        }
        return LocalDate.parse(s);
    }

}
