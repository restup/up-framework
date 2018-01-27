package com.github.restup.path;

/**
 * {@link PathValue} representing an invalid path.
 */
public class InvalidPathValue extends ConstantPathValue {

    InvalidPathValue(String field) {
        super(field, false);
    }

}
