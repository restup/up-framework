package com.github.restup.mapping.fields.composition;

import com.github.restup.annotations.field.RelationshipType;

abstract class BasicRelation implements Relation {

    private final String name;
    private final RelationshipType type;
    private final String joinField;
    private final boolean includable;
    private final boolean validateReferences;

    BasicRelation(String name, RelationshipType type, String joinField, boolean includable, boolean validateReferences) {
        this.name = name;
        this.type = type;
        this.joinField = joinField;
        this.includable = includable;
        this.validateReferences = validateReferences;
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

}
