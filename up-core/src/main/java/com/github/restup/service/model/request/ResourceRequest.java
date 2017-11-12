package com.github.restup.service.model.request;

import com.github.restup.registry.Resource;

/**
 * Provides access to the requested resource. Common to all request interfaces.
 *
 * @param <T>
 */
public interface ResourceRequest<T> {
    /**
     * The requested resource
     *
     * @return
     */
    Resource<?, ?> getResource();
}
