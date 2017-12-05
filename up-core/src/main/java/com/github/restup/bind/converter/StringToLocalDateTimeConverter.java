package com.github.restup.bind.converter;

import com.github.restup.errors.ErrorFactory;
import java.time.LocalDateTime;
import org.apache.commons.lang3.StringUtils;

public class StringToLocalDateTimeConverter extends StringConverter<LocalDateTime> {

    private static final String DEFAULT_TIME = "T00:00:00Z";

    private final boolean lenient;

    public StringToLocalDateTimeConverter(ErrorFactory errorFactory) {
        this(errorFactory, true);
    }

    public StringToLocalDateTimeConverter(ErrorFactory errorFactory, boolean lenient) {
        super(errorFactory, LocalDateTime.class);
        this.lenient = lenient;
    }

    @Override
    LocalDateTime convertValue(String s) {
        String d = s;
        if (lenient) {
            if (s.length() == 10) {
                d = StringUtils.join(s, DEFAULT_TIME);
            }
        }
        return LocalDateTime.parse(d);

    }

}
