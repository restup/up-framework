package com.github.restup.service.model.request;

import com.github.restup.path.ResourcePath;
import com.github.restup.service.model.ResourceData;

import java.util.List;

/**
 * Common interface for {@link CreateRequest} and {@link UpdateRequest} operations
 *
 * @param <T>
 * @author abuttaro
 */
public interface PersistenceRequest<T> extends ResourceRequest<T>, ResourceData<T> {

    /**
     * The paths that are included in the request
     *
     * @return
     */
    List<ResourcePath> getRequestedPaths();

    /**
     * Tests if the {@link #getRequestedPaths()} includes the path specified. An exact match on
     * path is not required. For example if path is "foo", true is returned if {@link #getRequestedPaths()}
     * includes "foo" or any subpath of "foo" - ex "foo.bar".
     *
     * @param path
     * @return
     */
    boolean hasPath(ResourcePath path);

}
