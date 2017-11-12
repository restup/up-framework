package com.github.restup.bind.converter;

import com.github.restup.errors.ErrorBuilder;
import com.github.restup.errors.ErrorFactory;
import com.github.restup.errors.Errors;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

public class StringToBooleanConverter extends StringConverter<Boolean> {

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

    protected StringToBooleanConverter(ErrorFactory errorFactory) {
        super(errorFactory, Boolean.class, Boolean.TYPE);
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

    @Override
    protected Boolean convertValue(String parameterName, String source, Errors errors) {

        String value = source.trim();
        if (!StringUtils.isEmpty(source)) {
            value = value.toLowerCase();
            if (trueValues.contains(value)) {
                return Boolean.TRUE;
            } else if (falseValues.contains(value)) {
                return Boolean.FALSE;
            }
        }
        errors.addError(
                ErrorBuilder.builder()
                        .source(
                                getErrorFactory()
                                        .createParameterError(parameterName, value))

        );
        return null;
    }

    @Override
    Boolean convertValue(String from) {
        return null;
    }
}
