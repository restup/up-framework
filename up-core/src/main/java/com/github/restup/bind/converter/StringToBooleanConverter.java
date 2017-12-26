package com.github.restup.bind.converter;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class StringToBooleanConverter {

    private static final Set<String> trueValues;
    private static final Set<String> falseValues;

    static {
        trueValues = new HashSet<String>(6);
        trueValues.add("true");
        trueValues.add("on");
        trueValues.add("yes");
        trueValues.add("1");
        trueValues.add("t");
        trueValues.add("y");

        falseValues = new HashSet<String>(6);
        falseValues.add("false");
        falseValues.add("off");
        falseValues.add("no");
        falseValues.add("0");
        falseValues.add("n");
        falseValues.add("f");
    }


    public static boolean isTrue(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            return trueValues.contains(((String) value).toLowerCase());
        }
        return false;
    }

    public static Boolean toBoolean(String from) {
        String value = from.trim();
        if (!StringUtils.isEmpty(from)) {
            value = value.toLowerCase();
            if (trueValues.contains(value)) {
                return Boolean.TRUE;
            } else if (falseValues.contains(value)) {
                return Boolean.FALSE;
            }
        }
        throw new IllegalArgumentException(from+" is not a valid boolean value");
    }

}
