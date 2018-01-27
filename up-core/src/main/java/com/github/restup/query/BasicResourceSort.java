package com.github.restup.query;

import java.util.Objects;
import com.github.restup.path.ResourcePath;
import com.github.restup.registry.Resource;
import com.github.restup.util.Assert;

/**
 * Specifies sort behavior for a resource query
 */
class BasicResourceSort implements ResourceSort {

    private final ResourcePath path;
    private final Boolean ascending;

    BasicResourceSort(ResourcePath path, Boolean ascending) {
        super();
        Assert.notNull(path, "path cannot be null");
        this.path = path;
        this.ascending = ascending;
    }

    BasicResourceSort(ResourcePath path) {
        this(path, true);
    }

    BasicResourceSort(Resource<?, ?> resource, String beanPath) {
        this(ResourcePath.path(resource, beanPath));
    }

    /**
     * @return true if ascending is true or null, false otherwise
     */
    @Override
    public boolean isAscending() {
        return !Objects.equals(Boolean.FALSE, ascending);
    }

    /**
     * true if ascending, false if descending, null if unspecified
     */
    @Override
    public Boolean getAscending() {
        return ascending;
    }

    /**
     * @return path to be sorted
     */
    @Override
    public ResourcePath getPath() {
        return path;
    }

}
