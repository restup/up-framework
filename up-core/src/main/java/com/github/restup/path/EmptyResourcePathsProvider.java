package com.github.restup.path;

import com.github.restup.registry.Resource;

import java.util.Collections;
import java.util.List;

public class EmptyResourcePathsProvider implements ResourcePathsProvider {

    @Override
    @SuppressWarnings("unchecked")
    public List<ResourcePath> getPaths(Resource<?, ?> resource) {
        return Collections.EMPTY_LIST;
    }

}
