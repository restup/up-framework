package com.github.restup.controller.request.parser;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.request.parser.path.RequestPathParserResult;
import com.github.restup.query.ResourceQuery;
import com.github.restup.registry.ResourceRelationship;
import java.util.Collection;

/**
 * Adds filter criteria based upon relationships specified by requests.
 *
 * @author abuttaro
 */
public class DefaultRelationshipsParser implements RequestParser {

    @Override
    public void parse(ResourceControllerRequest request,
        RequestPathParserResult requestPathParserResult,
        ParsedResourceControllerRequest.Builder<?> builder) {
        if (!builder.hasErrors()) {
            ResourceRelationship relationship = requestPathParserResult.getResourceRelationship();
            if (relationship != null) {
                // ids from path are default criteria
                Collection joinIds = requestPathParserResult.getIds();

                if (relationship.isFrom(requestPathParserResult.getRelationship())) {
                    // have to look up resource by id and find filter criteria
                    Object result = ResourceQuery.find(requestPathParserResult.getRelationship(),
                        getId(requestPathParserResult));
                    joinIds = relationship.getIds(result);
                    builder.addFilter(relationship.getToPaths(), joinIds);
                } else {
                    builder.addFilter(relationship.getFromPaths(), joinIds);
                }
            }
        }
    }

    Object getId(RequestPathParserResult request) {
        return request.getIds().iterator().next();
    }

}
