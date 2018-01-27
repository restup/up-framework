package com.github.restup.path;

import java.util.Collections;
import java.util.List;
import com.github.restup.registry.Resource;

public class EmptyResourcePathsProvider implements ResourcePathsProvider {

    @Override
    public List<ResourcePath> getPaths(Resource<?, ?> resource) {
        return Collections.emptyList();
    }

}
