package com.github.restup.path;

import java.util.List;
import com.github.restup.registry.Resource;

public interface ResourcePathsProvider {

    List<ResourcePath> getPaths(Resource<?, ?> resource);

    static ResourcePathsProvider empty() {
        return EmptyResourcePathsProvider.getInstance();
    }

    static ResourcePathsProvider allApiFields() {
        return new AllResourcePathsProvider(false, true);
    }

    static ResourcePathsProvider allFields() {
        return new AllResourcePathsProvider(true, true);
    }

}
