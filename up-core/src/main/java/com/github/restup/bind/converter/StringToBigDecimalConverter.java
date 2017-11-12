package com.github.restup.bind.converter;

import com.github.restup.errors.ErrorFactory;

import java.math.BigDecimal;

public class StringToBigDecimalConverter extends StringConverter<BigDecimal> {

    public StringToBigDecimalConverter(ErrorFactory errorFactory) {
        super(errorFactory, BigDecimal.class);
    }

    @Override
    BigDecimal convertValue(String from) {
        return new BigDecimal(from);
    }

}
