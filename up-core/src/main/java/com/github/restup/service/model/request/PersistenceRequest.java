package com.github.restup.service.model.request;

import java.util.List;
import com.github.restup.path.ResourcePath;
import com.github.restup.service.model.ResourceData;

/**
 * Common interface for {@link CreateRequest} and {@link UpdateRequest} operations
 *
 * @author abuttaro
 */
public interface PersistenceRequest<T> extends ResourceRequest<T>, ResourceData<T> {

    /**
     * The paths that are included in the request
     * 
     * @return paths
     */
    List<ResourcePath> getRequestedPaths();

    /**
     * Tests if the {@link #getRequestedPaths()} includes the path specified. An exact match on path is
     * not required. For example if path is "foo", true is returned if {@link #getRequestedPaths()}
     * includes "foo" or any subpath of "foo" - ex "foo.bar".
     * 
     * @param path to find
     * @return true if path is included, false otherwise
     */
    boolean hasPath(ResourcePath path);

}
