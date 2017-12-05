package com.github.restup.path;

import com.github.restup.registry.Resource;
import java.util.List;

public class AllResourcePathsProvider implements ResourcePathsProvider {

    private final boolean includeTransient;
    private final boolean apiFieldsOnly;

    public AllResourcePathsProvider(boolean includeTransient, boolean apiFieldsOnly) {
        super();
        this.includeTransient = includeTransient;
        this.apiFieldsOnly = apiFieldsOnly;
    }

    public AllResourcePathsProvider() {
        this(true, true);
    }

    public static ResourcePathsProvider getDefaultSparseFieldsProvider() {
        return new AllResourcePathsProvider(false, true);
    }

    @Override
    public List<ResourcePath> getPaths(Resource<?, ?> resource) {
        return Resource.getPaths(resource, includeTransient, apiFieldsOnly);
    }

}
