package com.github.restup.bind.converter;

import com.github.restup.errors.ErrorFactory;

import java.math.BigInteger;

public class StringToBigIntegerConverter extends StringConverter<BigInteger> {

    protected StringToBigIntegerConverter(ErrorFactory errorFactory) {
        super(errorFactory, BigInteger.class);
    }

    @Override
    BigInteger convertValue(String from) {
        return new BigInteger(from);
    }

}
