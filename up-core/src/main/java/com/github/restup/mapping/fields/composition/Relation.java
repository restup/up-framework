package com.github.restup.mapping.fields.composition;

import static com.github.restup.util.UpUtils.nvl;

import com.github.restup.annotations.field.Relationship;
import com.github.restup.annotations.field.RelationshipType;
import com.github.restup.registry.ResourceRegistry;

public interface Relation {

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

    String getName();

    RelationshipType getType();

    String getJoinField();

    boolean isIncludable();

    boolean isValidateReferences();

    String getResource(ResourceRegistry registry);

    final static class Builder {

        private String name;
        private RelationshipType type;
        private String joinField;
        private boolean includable;
        private boolean validateReferences;
        private String resourceName;
        private Class<?> resourceClass;
        
        private Builder() {
            includable = true;
            validateReferences = true;
        }

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

        public Builder resource(String resource) {
            this.resourceName = resource;
            return me();
        }

        public Builder resource(Class<?> resource) {
            this.resourceClass = resource;
            return me();
        }

        public Relation build() {
        		String joinField = nvl(this.joinField,"id");
        		RelationshipType type = nvl(this.type, RelationshipType.manyToOne);
        		String resourceName = this.resourceName;
        		if ( resourceClass != null ) {
        			return new BasicTypedRelation(name, type, joinField, includable, validateReferences, resourceClass);
        	    }
            return new BasicNamedRelation(name, type, joinField, includable, validateReferences, resourceName);
        }
    }
}
