package com.github.restup.path;

import java.util.List;
import com.github.restup.registry.Resource;

class AllResourcePathsProvider implements ResourcePathsProvider {

    private final boolean includeTransient;
    private final boolean apiFieldsOnly;

    AllResourcePathsProvider(boolean includeTransient, boolean apiFieldsOnly) {
        super();
        this.includeTransient = includeTransient;
        this.apiFieldsOnly = apiFieldsOnly;
    }

    AllResourcePathsProvider() {
        this(true, true);
    }

    @Override
    public List<ResourcePath> getPaths(Resource<?, ?> resource) {
        return Resource.getPaths(resource, includeTransient, apiFieldsOnly);
    }

}
