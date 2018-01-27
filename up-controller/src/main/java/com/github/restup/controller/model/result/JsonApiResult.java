package com.github.restup.controller.model.result;

import com.github.restup.annotations.field.RelationshipType;
import com.github.restup.controller.linking.Link;
import com.github.restup.controller.linking.LinkBuilder;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.registry.Resource;
import java.util.List;

/**
 * Result wrapper used to identify custom serialization targeted for JSON API formatted results
 */
public class JsonApiResult extends NegotiatedResult {

    public final static String ID = "id";
    public final static String TYPE = "type";
    public final static String ATTRIBUTES = "attributes";
    public final static String RELATIONSHIPS = "relationships";
    public final static String LINKS = "links";

    private final LinkBuilder linkBuilder;

    public JsonApiResult(LinkBuilder linkBuilder, ParsedResourceControllerRequest<?> request, Object result) {
        super(request, result);
        this.linkBuilder = linkBuilder;
    }

    public List<Link> getTopLevelLinks() {
        return linkBuilder.getTopLevelLinks(getRequest(), getResult());
    }

    public List<Link> getLinks(Resource<?, ?> resource, Object id) {
        return linkBuilder.getLinks(getRequest(), getResult(), resource, id);
    }

    public List<Link> getRelationshipLinks(Resource<?, ?> resource, Object id) {
        return linkBuilder.getRelationshipLinks(getRequest(), getResult(), resource, id);
    }

    public List<Link> getRelationshipLinks(Resource<?, ?> relationship, Object id, RelationshipType type) {
        return linkBuilder.getRelationshipLinks(getRequest(), getResult(), relationship, id, type);
    }
    
    public LinkBuilder getLinkBuilder() {
        return linkBuilder;
    }

}
