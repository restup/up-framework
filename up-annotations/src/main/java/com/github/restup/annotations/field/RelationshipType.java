package com.github.restup.annotations.field;

public enum RelationshipType {
    oneToOne, oneToMany, manyToOne, manyToMany;

    public static boolean isOneTo(RelationshipType type) {
        return type == oneToOne || type == oneToMany;
    }

    public static boolean isManyTo(RelationshipType type) {
        return !isOneTo(type);
    }

    public static boolean isToOne(RelationshipType type) {
        return type == oneToOne || type == manyToOne;
    }

    public static boolean isToMany(RelationshipType type) {
        return !isToOne(type);
    }
}
