package com.github.restup.bind.converter;

import com.github.restup.errors.ErrorFactory;
import java.time.ZonedDateTime;
import java.util.Date;

public class StringToDateConverter extends StringConverter<Date> {

    private final StringToZonedDateTimeConverter converter;

    public StringToDateConverter(ErrorFactory errorFactory) {
        this(errorFactory, new StringToZonedDateTimeConverter(errorFactory));
    }

    public StringToDateConverter(ErrorFactory errorFactory, StringToZonedDateTimeConverter converter) {
        super(errorFactory, Date.class);
        this.converter = converter;
    }

    @Override
    Date convertValue(String s) {
        ZonedDateTime zdt = converter.convertValue(s);
        return Date.from(zdt.toInstant());
    }

}
