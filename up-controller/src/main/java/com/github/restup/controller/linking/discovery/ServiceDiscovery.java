package com.github.restup.controller.linking.discovery;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.registry.Resource;

/**
 * Interface for locating resource endpoints.
 */
public interface ServiceDiscovery {

    /**
     * Locate the url of the requested resource.
     *
     * @param request details of the current request
     * @param resource to discover service location
     * @return The fully qualified url of the resource
     */
    String locateResourceUrl(ParsedResourceControllerRequest<?> request, Resource<?, ?> resource);

}
