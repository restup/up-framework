package com.github.restup.controller.request.parser.path;

import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRelationship;
import java.util.List;

class BasicRequestPathParserResult implements RequestPathParserResult {

    private final Resource<?, ?> resource;
    private final Resource<?, ?> relationship;
    private final ResourceRelationship<?, ?, ?, ?> resourceRelationship;
    private final List<?> ids;

    BasicRequestPathParserResult(Resource<?, ?> resource,
        Resource<?, ?> relationship,
        ResourceRelationship<?, ?, ?, ?> resourceRelationship, List<?> ids) {
        this.resource = resource;
        this.relationship = relationship;
        this.resourceRelationship = resourceRelationship;
        this.ids = ids;
    }

    @Override
    public Resource<?, ?> getResource() {
        return resource;
    }

    @Override
    public Resource<?, ?> getRelationship() {
        return relationship;
    }

    @Override
    public ResourceRelationship<?, ?, ?, ?> getResourceRelationship() {
        return resourceRelationship;
    }

    @Override
    public List<?> getIds() {
        return ids;
    }
}
