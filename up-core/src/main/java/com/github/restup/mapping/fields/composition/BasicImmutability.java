package com.github.restup.mapping.fields.composition;

import java.util.Objects;

class BasicImmutability implements Immutability {

    private final boolean immutable;
    private final boolean errorOnUpdateAttempt;

    BasicImmutability(boolean immutable, boolean errorOnUpdateAttempt) {
        this.immutable = immutable;
        this.errorOnUpdateAttempt = errorOnUpdateAttempt;
    }

    @Override
    public boolean isImmutable() {
        return immutable;
    }

    @Override
    public boolean isErrorOnUpdateAttempt() {
        return errorOnUpdateAttempt;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (! ( o instanceof BasicImmutability )) {
            return false;
        }
        BasicImmutability that = (BasicImmutability) o;
        return errorOnUpdateAttempt == that.errorOnUpdateAttempt &&
                immutable == that.immutable;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(immutable, errorOnUpdateAttempt);
    }

}
