package com.github.restup.controller.linking;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.annotations.field.RelationshipType;
import com.github.restup.registry.Resource;

import java.util.List;

/**
 * A {@link LinkBuilder} provides all of the links required for a response.
 */
public interface LinkBuilder {

    /**
     * @param request
     * @param result
     * @return the top level links to return for the document.
     */
    List<Link> getTopLevelLinks(ParsedResourceControllerRequest<?> request, Object result);

    /**
     * @param request
     * @param Result
     * @param resource
     * @param id
     * @return item links for the resource and id specified
     */
    List<Link> getLinks(ParsedResourceControllerRequest<?> request, Object Result, Resource<?, ?> resource, Object id);

    /**
     * @param request
     * @param Result
     * @param relationship
     * @param id
     * @return the relationship links
     */
    List<Link> getRelationshipLinks(ParsedResourceControllerRequest<?> request, Object Result, Resource<?, ?> relationship, Object id);

    List<Link> getRelationshipLinks(ParsedResourceControllerRequest<?> request, Object result, Resource<?, ?> relationship, Object id, RelationshipType type);
}
