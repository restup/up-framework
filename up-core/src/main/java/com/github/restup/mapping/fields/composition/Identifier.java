package com.github.restup.mapping.fields.composition;

public interface Identifier {

    boolean isNonAutoGeneratedValuePermitted();
    
    static Builder builder() {
    		return new Builder();
    }

    static class Builder {
        private boolean nonAutoGeneratedValuePermitted;

        private Builder me() {
            return this;
        }
        
        public Builder monAutoGeneratedValuePermitted(boolean nonAutoGeneratedValuePermitted) {
			this.nonAutoGeneratedValuePermitted = nonAutoGeneratedValuePermitted;
			return me();
		}

        public Identifier build() {
            return BasicIdentifier.of(nonAutoGeneratedValuePermitted);
        }
    }

}
