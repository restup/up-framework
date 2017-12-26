package com.github.restup.bind.converter;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.function.Function;

public class StringToDateConverter implements Function<String,Date> {

    private final StringToZonedDateTimeConverter converter;

    public StringToDateConverter() {
        this(new StringToZonedDateTimeConverter());
    }

    public StringToDateConverter(StringToZonedDateTimeConverter converter) {
        super();
        this.converter = converter;
    }

    @Override
    public Date apply(String s) {
        ZonedDateTime zdt = converter.apply(s);
        return Date.from(zdt.toInstant());
    }

}
