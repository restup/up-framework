package com.github.restup.mapping.fields.composition;

import com.github.restup.annotations.field.RelationshipType;

abstract class AbstractBasicRelation implements Relation {

    private final String name;
    private final RelationshipType type;
    private final String joinField;
    private final boolean includable;
    private final boolean validateReferences;

    AbstractBasicRelation(String name, RelationshipType type, String joinField, boolean includable, boolean validateReferences) {
        this.name = name;
        this.type = type;
        this.joinField = joinField;
        this.includable = includable;
        this.validateReferences = validateReferences;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public RelationshipType getType() {
        return type;
    }

    @Override
    public String getJoinField() {
        return joinField;
    }

    @Override
    public boolean isIncludable() {
        return includable;
    }

    @Override
    public boolean isValidateReferences() {
        return validateReferences;
    }

}
