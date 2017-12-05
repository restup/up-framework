package com.github.restup.service.model.request;

import com.github.restup.query.ResourceQueryStatement;
import java.util.List;

/**
 * Defines required fields for query operations, providing query criteria for list operations or targeted updates or deletes
 *
 * @author abuttaro
 */
public interface QueryRequest {

    /**
     * Includes requests for secondary data related to the primary requests results.
     */
    List<ResourceQueryStatement> getSecondaryQueries();

    /**
     * The primary query request
     */
    ResourceQueryStatement getQuery();

}
