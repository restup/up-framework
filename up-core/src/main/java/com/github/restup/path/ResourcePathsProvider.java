package com.github.restup.path;

import com.github.restup.registry.Resource;

import java.util.List;

public interface ResourcePathsProvider {

    List<ResourcePath> getPaths(Resource<?, ?> resource);

}
