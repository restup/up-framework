package com.github.restup.mapping.fields.composition;

public interface Identifier {

    static Builder builder() {
        return new Builder();
    }

    boolean isClientGeneratedIdentifierPermitted();

    class Builder {

        private boolean clientGeneratedIdentifierPermitted;

        private Builder me() {
            return this;
        }

        public Builder clientGeneratedIdentifierPermitted(
            boolean clientGeneratedIdentifierPermitted) {
            this.clientGeneratedIdentifierPermitted = clientGeneratedIdentifierPermitted;
            return me();
        }

        public Identifier build() {
            return BasicIdentifier.of(clientGeneratedIdentifierPermitted);
        }
    }

}
