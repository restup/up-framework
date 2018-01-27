package com.github.restup.controller.linking;

/**
 * A simple named link
 */
public class BasicLink implements Link {

    private final String name;
    private final String href;

    public BasicLink(String name, String href) {
        this.name = name;
        this.href = href;
    }

    public BasicLink(LinkRelations rel, String href) {
        this(rel.getName(), href);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getHref() {
        return href;
    }
}
