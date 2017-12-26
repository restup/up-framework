package com.github.restup.bind.converter;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

public class StringToZonedDateTimeConverter implements Function<String, ZonedDateTime> {

    private static final String DEFAULT_ZONE = "Z";
    private static final String DEFAULT_TIME = "T00:00:00Z";

    private final boolean lenient;
    private final boolean acceptEpochMillis;
    private final boolean acceptEpochSeconds;

    public StringToZonedDateTimeConverter() {
        this(true, true, false);
    }

    public StringToZonedDateTimeConverter(boolean lenient, boolean acceptEpochMillis, boolean acceptEpochSeconds) {
        super();
        this.lenient = lenient;
        this.acceptEpochMillis = acceptEpochMillis;
        this.acceptEpochSeconds = acceptEpochSeconds;
        if (acceptEpochMillis && acceptEpochSeconds) {
            throw new IllegalStateException("epoch millis or seconds can be supported, not both");
        }
    }

    @Override
    public ZonedDateTime apply(String s) {
        String d = s;
        if (lenient) {
            int l = s.length();
            if (l == 10) {
                d = StringUtils.join(s, DEFAULT_TIME);
            } else if (l == 16 || l == 19) {
                d = StringUtils.join(s, DEFAULT_ZONE);
            }
        }
        try {
            return ZonedDateTime.parse(d);
        } catch (DateTimeParseException e) {
            if (acceptEpochMillis) {
                try {
                    long l = Long.valueOf(s);
                    return Instant.ofEpochMilli(l).atZone(ZoneOffset.UTC);
                } catch (NumberFormatException nfe) {
                    // ignore, rethrow DateTimeParseException
                }
            } else if (acceptEpochSeconds) {
                try {
                    long l = Long.valueOf(s);
                    return Instant.ofEpochSecond(l).atZone(ZoneOffset.UTC);
                } catch (NumberFormatException nfe) {
                    // ignore, rethrow DateTimeParseException
                }
            }
            throw e;
        }
    }

}
