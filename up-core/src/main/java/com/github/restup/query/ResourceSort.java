package com.github.restup.query;

import com.github.restup.path.ResourcePath;
import com.github.restup.registry.Resource;
import com.github.restup.util.Assert;
import java.util.Objects;

/**
 * Specifies sort behavior for a resource query
 */
public class ResourceSort {

    private final ResourcePath path;
    private final Boolean ascending;

    public ResourceSort(ResourcePath path, Boolean ascending) {
        super();
        Assert.notNull(path, "path cannot be null");
        this.path = path;
        this.ascending = ascending;
    }

    public ResourceSort(ResourcePath path) {
        this(path, true);
    }

    public ResourceSort(Resource<?, ?> resource, String beanPath) {
        this(ResourcePath.path(resource, beanPath));
    }

    public static Boolean isAscending(char c) {
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
    public boolean isAscending() {
        return !Objects.equals(Boolean.FALSE, ascending);
    }

    /**
     * true if ascending, false if descending, null if unspecified
     */
    public Boolean getAscending() {
        return ascending;
    }

    /**
     * @return path to be sorted
     */
    public ResourcePath getPath() {
        return path;
    }

}
