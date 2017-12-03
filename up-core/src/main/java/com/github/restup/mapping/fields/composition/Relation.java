package com.github.restup.mapping.fields.composition;

import com.github.restup.annotations.field.Relationship;
import com.github.restup.annotations.field.RelationshipType;

public interface Relation {

    public String getName();

    public RelationshipType getType();

    public String getJoinField();

    public boolean isIncludable();

    public boolean isValidateReferences() ;

    public Class<?> getResource();

    static Relation getRelation(Relationship ann) {
        return ann == null ? null
        		: builder()
        		.name(ann.name())
        		.type(ann.type())
        		.joinField(ann.joinField())
        		.includable(ann.includable())
        		.validateReferences(ann.validateReferences())
        		.resource(ann.resource()).build();
    }
    
    static Builder builder() {
    		return new Builder();
    }

    public static class Builder {
        private String name;
        private RelationshipType type;
        private String joinField;
        private boolean includable;
        private boolean validateReferences;
        private Class<?> resource;

        private Builder me() {
            return this;
        }

        public Builder name(String name) {
			this.name = name;
			return me();
		}

		public Builder type(RelationshipType type) {
			this.type = type;
			return me();
		}

		public Builder joinField(String joinField) {
			this.joinField = joinField;
			return me();
		}

		public Builder includable(boolean includable) {
			this.includable = includable;
			return me();
		}

		public Builder validateReferences(boolean validateReferences) {
			this.validateReferences = validateReferences;
			return me();
		}

		public Builder resource(Class<?> resource) {
			this.resource = resource;
			return me();
		}

		public Relation build() {
            return new BasicRelation(name, type, joinField, includable, validateReferences, resource);
        }
    }
}
