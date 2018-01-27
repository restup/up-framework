package com.github.restup.path;

import java.util.Objects;
import com.github.restup.util.Assert;

/**
 * {@link PathValue} which is contstant
 */
class ConstantPathValue implements PathValue {

    private final String field;
    private final boolean reservedPath;

    ConstantPathValue(String field, boolean reservedPath) {
        Assert.notNull(field, "field is required");
        this.field = field;
        this.reservedPath = reservedPath;
    }

    ConstantPathValue(String field) {
        this(field, true);
    }

    @Override
    public boolean isReservedPath() {
        return reservedPath;
    }

    @Override
    public String getApiPath() {
        return field;
    }

    @Override
    public String getBeanPath() {
        return field;
    }

    @Override
    public String getPersistedPath() {
        return field;
    }

    @Override
    public String toString() {
        return field;
    }

    @Override
    public boolean supportsType(Class<?> instance) {
        return false;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(field);
    }

    public String getField() {
        return field;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConstantPathValue)) {
            return false;
        }
        ConstantPathValue other = (ConstantPathValue) o;
        return Objects.equals(field, other.field);
    }

}
