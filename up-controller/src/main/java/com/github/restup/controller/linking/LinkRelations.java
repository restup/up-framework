package com.github.restup.controller.linking;

/**
 * Standard link relations https://www.iana.org/assignments/link-relations/link-relations.xhtml http://jsonapi.org/format/#fetching-pagination
 */
public enum LinkRelations {
    first("first"),
    last("last"),
    prev("prev"),
    next("next"),
    self("self"),
    related("related");

    private final String name;

    LinkRelations(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
