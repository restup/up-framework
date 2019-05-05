package com.github.restup.controller.request.parser.params;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@FunctionalInterface
public interface ParameterValueValidator {

    boolean isValid(String scrubbedParameterValue);

    enum ParameterValueValidators implements ParameterValueValidator {
        NOOP {
            @Override
            public boolean isValid(String scrubbedParameterValue) {
                return true;
            }
        },
        IS_NOT_EMPTY {
            @Override
            public boolean isValid(String scrubbedParameterValue) {
                return isNotEmpty(scrubbedParameterValue);
            }
        },
        IS_NOT_BLANK {
            @Override
            public boolean isValid(String scrubbedParameterValue) {
                return isNotBlank(scrubbedParameterValue);
            }
        }
    }

}
