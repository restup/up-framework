package com.github.restup.controller.content.negotiation;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerResponse;

/**
 * Provides a default content negotiator to handle any request, delegating to another content negotiator
 */
public class DefaultContentNegotiator implements ContentNegotiator {

    private ContentNegotiator contentNegotiator;

    public DefaultContentNegotiator(ContentNegotiator contentNegotiator) {
        this.contentNegotiator = contentNegotiator;
    }

    /**
     * Delegates to the {@link ContentTypeNegotiation} found for the specified media type
     *
     * @throws IllegalArgumentException if mediaType is not found in the provided negotiators
     */
    public DefaultContentNegotiator(String mediaType, ContentNegotiator... negotiators) throws IllegalArgumentException {
        this(getContentNegotiator(mediaType, negotiators));
    }

    private static ContentNegotiator getContentNegotiator(String mediaType, ContentNegotiator[] negotiators) {
        for (ContentNegotiator negotiator : negotiators) {
            if (negotiator instanceof ContentTypeNegotiation) {
                if (((ContentTypeNegotiation) negotiator).getContentType().equals(mediaType)) {
                    return negotiator;
                }
            }
        }
        throw new IllegalArgumentException("Negotiator does not exist for  " + mediaType);
    }

    /**
     * Accepts all requests
     *
     * @return true always
     */
    @Override
    public <T> boolean accept(ResourceControllerRequest request) {
        return true;
    }

    @Override
    public <T> Object formatResponse(ParsedResourceControllerRequest<T> request, ResourceControllerResponse response, Object result) {
        return contentNegotiator.formatResponse(request, response, result);
    }

}
