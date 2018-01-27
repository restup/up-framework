package com.github.restup.bind.converter;

import java.time.LocalDateTime;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

public class StringToLocalDateTimeConverter implements Function<String,LocalDateTime> {

    private static final String DEFAULT_TIME = "T00:00:00";

    private final boolean lenient;

    public StringToLocalDateTimeConverter() {
        this(true);
    }

    public StringToLocalDateTimeConverter(boolean lenient) {
        this.lenient = lenient;
    }

    @Override
    public LocalDateTime apply(String s) {
        String d = s;
        if (lenient) {
            if (s.length() == 10) {
                d = StringUtils.join(s, DEFAULT_TIME);
            }
        }
        return LocalDateTime.parse(d);

    }

}
