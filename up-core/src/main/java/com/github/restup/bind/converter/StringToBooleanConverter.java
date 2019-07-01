package com.github.restup.bind.converter;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.HashSet;
import java.util.Set;

public class StringToBooleanConverter {

    private static final Set<String> trueValues;
    private static final Set<String> falseValues;

    static {
        trueValues = new HashSet<>(6);
        trueValues.add("true");
        trueValues.add("on");
        trueValues.add("yes");
        trueValues.add("1");
        trueValues.add("t");
        trueValues.add("y");

        falseValues = new HashSet<>(6);
        falseValues.add("false");
        falseValues.add("off");
        falseValues.add("no");
        falseValues.add("0");
        falseValues.add("n");
        falseValues.add("f");
    }


    private StringToBooleanConverter() {
        super();
    }

    public static boolean isTrue(Object value) {
        return isTrue(value, false);
    }

    public static boolean isTrue(Object value, boolean defaultValue) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            return trueValues.contains(((String) value).toLowerCase());
        }
        return defaultValue;
    }

    public static Boolean toBoolean(String from) {
        if (from != null) {
            String value = from.trim();
            if (isNotEmpty(from)) {
                value = value.toLowerCase();
                if (trueValues.contains(value)) {
                    return Boolean.TRUE;
                } else if (falseValues.contains(value)) {
                    return Boolean.FALSE;
                }
            }
        }
        throw new IllegalArgumentException(from + " is not a valid boolean value");
    }
}
