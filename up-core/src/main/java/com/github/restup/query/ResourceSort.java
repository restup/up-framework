package com.github.restup.query;

import java.util.Objects;
import com.github.restup.path.ResourcePath;
import com.github.restup.registry.Resource;

/**
 * Specifies sort behavior for a resource query
 */
public interface ResourceSort {

    static ResourceSort of(ResourcePath path, Boolean ascending) {
        return new BasicResourceSort(path, ascending);
    }

    static ResourceSort of(ResourcePath path) {
        return of(path, true);
    }

    static ResourceSort of(Resource<?, ?> resource, String beanPath) {
        return of(ResourcePath.path(resource, beanPath));
    }

    static ResourceSort of(Resource<?, ?> resource, String beanPath, Boolean ascending) {
        return of(ResourcePath.path(resource, beanPath), ascending);
    }

    static Boolean isAscending(char c) {
        if (c == '+') {
            return true;
        }
        if (c == '-') {
            return false;
        }
        return null;
    }

    /**
     * @return true if ascending is true or null, false otherwise
     */
    default boolean isAscending() {
        return !Objects.equals(Boolean.FALSE, getAscending());
    }

    /**
     * @return true if ascending, false if descending, null if unspecified
     */
    Boolean getAscending();

    /**
     * @return path to be sorted
     */
    public ResourcePath getPath();

}
