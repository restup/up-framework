package com.github.restup.controller.content.negotiation;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerResponse;

/**
 * Negotiates return types based upon request details.
 * <p>
 * While this may query serialization using libraries such as Jackson or Gson,
 * it is also likely that the object types and structure may have to change to modify
 * the response structure depending upon the request context.
 *
 * @author abuttaro
 */
public interface ContentNegotiator {

    /**
     * @param request
     * @return true if the controller will accept the request, false otherwise
     */
    <T> boolean accept(ResourceControllerRequest request);

    /**
     * Reformats an object for response.  This may be required to alter the response
     * for a different presentation (json, json api, hal, etc)
     *
     * @param request  producing the response
     * @param response for the request
     * @param result   result object of the operation
     * @return a new, possibly reformatted object
     */
    <T> Object formatResponse(ParsedResourceControllerRequest<T> request, ResourceControllerResponse response, Object result);

}
