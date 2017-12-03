package com.github.restup.mapping.fields.composition;

import com.github.restup.annotations.field.Immutable;

public interface Immutability {

    boolean isImmutable();

    /**
     * @return true if an error should occur when an update attempt is made for a read only field, false otherwise.
     */
    boolean isErrorOnUpdateAttempt();

    default boolean isIgnoreUpdateAttempt() {
        return !isErrorOnUpdateAttempt();
    }

    static Immutability getImmutability(Immutable immutable) {
        return immutable == null ? null
                : builder()
                .immutable(immutable.value())
                .errorOnUpdateAttempt(immutable.errorOnUpdateAttempt())
                .build();
    }

    static Builder builder() {
    		return new Builder();
    }

    public static class Builder {
    	
        private boolean immutable;
        private boolean errorOnUpdateAttempt;
        
        Builder() {
        		immutable = true;
        }

        private Builder me() {
            return this;
        }

        public Builder immutable(boolean immutable) {
			this.immutable = immutable;
			return me();
		}

		public Builder errorOnUpdateAttempt(boolean errorOnUpdateAttempt) {
			this.errorOnUpdateAttempt = errorOnUpdateAttempt;
			return me();
		}

		public Immutability build() {
            return new BasicImmutability(immutable, errorOnUpdateAttempt);
        }
    }
}
