package com.github.restup.controller.request.parser.params;

import static org.apache.commons.lang3.StringUtils.trim;

/**
 * Used by {@link ParameterValuesParser} Intended for basic input scrubbing such as trimming
 * values.
 */
@FunctionalInterface
public interface ParameterValueScrubber {

    String scrub(String rawParameterValue);

    enum ParameterValueScrubbers implements ParameterValueScrubber {
        NOOP {
            @Override
            public String scrub(String rawParameterValue) {
                return rawParameterValue;
            }
        },
        TRIMMED {
            @Override
            public String scrub(String rawParameterValue) {
                return trim(rawParameterValue);
            }
        }
    }

}
