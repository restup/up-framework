package com.github.restup.controller.request.parser.params;

import static com.github.restup.controller.request.parser.params.ParameterMatcher.ArrayMatcher.arrayMatcher;
import static com.github.restup.util.UpUtils.names;

import java.util.function.BiFunction;

/**
 * Matches a parameter name that is supported by a {@link ComposedRequestParamParser}
 */
@FunctionalInterface
interface ParameterMatcher {

    static ParameterMatcher eq(String value) {
        return (p) -> value.equals(p);
    }

    static ParameterMatcher startsWith(String... values) {
        return arrayMatcher((p, s) -> p.startsWith(s), values);
    }

    static <E extends Enum> ParameterMatcher startsWith(Class<? extends Enum<?>> e) {
        return startsWith(names(e));
    }

    static <E extends Enum> ParameterMatcher startsWith(String format, Class<? extends Enum<?>> e) {
        return startsWith(names(format, e));
    }

    static ParameterMatcher endsWith(String... values) {
        return arrayMatcher((p, s) -> p.endsWith(s), values);
    }

    static <E extends Enum> ParameterMatcher endsWith(Class<? extends Enum<?>> e) {
        return startsWith(names(e));
    }

    static <E extends Enum> ParameterMatcher endsWith(String format, Class<? extends Enum<?>> e) {
        return startsWith(names(format, e));
    }

    static ParameterMatcher matches(String... values) {
        return arrayMatcher((p, s) -> p.matches(s), values);
    }

    static <E extends Enum> ParameterMatcher matches(Class<? extends Enum<?>> e) {
        return startsWith(names(e));
    }

    static <E extends Enum> ParameterMatcher matches(String format, Class<? extends Enum<?>> e) {
        return startsWith(names(format, e));
    }

    boolean accept(String parameter);

    class ArrayMatcher implements ParameterMatcher {

        private final BiFunction<String, String, Boolean> matcher;
        private final String[] values;

        private ArrayMatcher(BiFunction<String, String, Boolean> matcher, String... values) {
            this.matcher = matcher;
            this.values = values;
        }

        public static ArrayMatcher arrayMatcher(BiFunction<String, String, Boolean> matcher,
            String... values) {
            return new ArrayMatcher(matcher, values);
        }

        @Override
        public boolean accept(String parameter) {
            for (String s : values) {
                if (matcher.apply(parameter, s)) {
                    return true;
                }
            }
            return false;
        }
    }

}
