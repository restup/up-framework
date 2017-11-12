package com.github.restup.path;

/**
 * {@link PathValue} representing an invalid path.
 */
public class InvalidPathValue extends ConstantPathValue {

    public InvalidPathValue(String field) {
        super(field, false);
    }

}
