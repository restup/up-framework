package com.github.restup.path;

import com.github.restup.util.Assert;

import java.util.Objects;

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

    public String getApiPath() {
        return field;
    }

    public String getBeanPath() {
        return field;
    }

    public String getPersistedPath() {
        return field;
    }

    @Override
    public String toString() {
        return field;
    }

    public boolean supportsType(Class<?> instance) {
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(field);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ConstantPathValue other = (ConstantPathValue) obj;
        if (field == null) {
            if (other.field != null)
                return false;
        } else if (!field.equals(other.field))
            return false;
        return true;
    }

}
