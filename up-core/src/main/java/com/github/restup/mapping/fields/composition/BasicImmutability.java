package com.github.restup.mapping.fields.composition;

import java.util.Objects;

class BasicImmutability implements Immutability {

    private final boolean immutable;
    private final boolean errorOnUpdateAttempt;

    BasicImmutability(boolean immutable, boolean errorOnUpdateAttempt) {
        this.immutable = immutable;
        this.errorOnUpdateAttempt = errorOnUpdateAttempt;
    }

    public boolean isImmutable() {
        return immutable;
    }

    public boolean isErrorOnUpdateAttempt() {
        return errorOnUpdateAttempt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BasicImmutability that = (BasicImmutability) o;
        return errorOnUpdateAttempt == that.errorOnUpdateAttempt &&
                immutable == that.immutable;
    }

    @Override
    public int hashCode() {
        return Objects.hash(immutable, errorOnUpdateAttempt);
    }

}
