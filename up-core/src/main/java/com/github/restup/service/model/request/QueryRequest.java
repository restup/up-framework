package com.github.restup.service.model.request;

import java.util.List;
import com.github.restup.query.ResourceQueryStatement;

/**
 * Defines required fields for query operations, providing query criteria for list operations or targeted updates or deletes
 *
 * @author abuttaro
 */
public interface QueryRequest {

    /**
     * Includes requests for secondary data related to the primary requests results.
     * 
     * @return list of secondary queries
     */
    List<ResourceQueryStatement> getSecondaryQueries();

    /**
     * The primary query request
     * 
     * @return query
     */
    ResourceQueryStatement getQuery();

}
