package com.github.restup.bind.converter;

import com.github.restup.errors.ErrorFactory;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

public class StringToZonedDateTimeConverter extends StringConverter<ZonedDateTime> {
    private static final String DEFAULT_ZONE = "Z";
    private static final String DEFAULT_TIME = "T00:00:00Z";

    private final boolean lenient;
    private final boolean acceptEpochMillis;
    private final boolean acceptEpochSeconds;

    public StringToZonedDateTimeConverter(ErrorFactory errorFactory) {
        this(errorFactory, true, true, false);
    }

    public StringToZonedDateTimeConverter(ErrorFactory errorFactory, boolean lenient, boolean acceptEpochMillis, boolean acceptEpochSeconds) {
        super(errorFactory, ZonedDateTime.class);
        this.lenient = lenient;
        this.acceptEpochMillis = acceptEpochMillis;
        this.acceptEpochSeconds = acceptEpochSeconds;
        if (acceptEpochMillis && acceptEpochSeconds) {
            throw new IllegalStateException("epoch millis or seconds can be supported, not both");
        }
    }

    @Override
    ZonedDateTime convertValue(String s) {
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
