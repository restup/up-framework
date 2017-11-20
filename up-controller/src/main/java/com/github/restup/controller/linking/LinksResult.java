package com.github.restup.controller.linking;

import java.util.Collection;

public class LinksResult {

    private final Collection<Link> links;

    public LinksResult(Collection<Link> links) {
        this.links = links;
    }

    public Collection<Link> getLinks() {
        return links;
    }
}
