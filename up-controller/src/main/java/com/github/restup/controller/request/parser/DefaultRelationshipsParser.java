package com.github.restup.controller.request.parser;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.query.ResourceQuery;
import com.github.restup.registry.ResourceRelationship;

import java.util.Collection;

/**
 * Adds filter criteria based upon relationships specified by requests.
 *
 * @author abuttaro
 */
public class DefaultRelationshipsParser implements RequestParser {

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void parse(ResourceControllerRequest request, ParsedResourceControllerRequest.Builder<?> builder) {
        if (!builder.hasErrors()) {
            ResourceRelationship relationship = request.getResourceRelationship();
            if (relationship != null) {
                // ids from path are default criteria
                Collection joinIds = request.getIds();

                if (relationship.isFrom(request.getRelationship())) {
                    // have to look up resource by id and find filter criteria
                    Object result = ResourceQuery.find(request.getRelationship(), getId(request));
                    joinIds = relationship.getIds(result);
                    builder.addFilter(relationship.getToPaths(), joinIds);
                } else {
                    builder.addFilter(relationship.getFromPaths(), joinIds);
                }
            }
        }
    }

    Object getId(ResourceControllerRequest request) {
        return request.getIds().iterator().next();
    }

}
