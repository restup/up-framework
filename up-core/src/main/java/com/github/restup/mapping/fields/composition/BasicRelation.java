package com.github.restup.mapping.fields.composition;

import com.github.restup.annotations.field.RelationshipType;

class BasicRelation implements Relation {

    private final String name;
    private final RelationshipType type;
    private final String joinField;
    private final boolean includable;
    private final boolean validateReferences;
    private final Class<?> resource;

    BasicRelation(String name, RelationshipType type, String joinField, boolean includable, boolean validateReferences, Class<?> resource) {
        this.name = name;
        this.type = type;
        this.joinField = joinField;
        this.includable = includable;
        this.validateReferences = validateReferences;
        this.resource = resource;
    }

    public String getName() {
        return name;
    }

    public RelationshipType getType() {
        return type;
    }

    public String getJoinField() {
        return joinField;
    }

    public boolean isIncludable() {
        return includable;
    }

    public boolean isValidateReferences() {
        return validateReferences;
    }

    public Class<?> getResource() {
        return resource;
    }

}
