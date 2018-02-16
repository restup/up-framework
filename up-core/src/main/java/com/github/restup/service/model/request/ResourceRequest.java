package com.github.restup.service.model.request;

import com.github.restup.registry.Resource;

/**
 * Provides access to the requested resource. Common to all request interfaces.
 */
public interface ResourceRequest<T> {

    /**
     * The requested resource
     * 
     * @return resource requested
     */
    Resource<?, ?> getResource();
}
