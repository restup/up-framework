package com.github.restup.path;

import java.util.Collections;
import java.util.List;
import com.github.restup.registry.Resource;

public class EmptyResourcePathsProvider implements ResourcePathsProvider {

    private static volatile EmptyResourcePathsProvider instance = null;

    private EmptyResourcePathsProvider() {
        super();
    }

    public static EmptyResourcePathsProvider getInstance() {
        if (instance == null) {
            synchronized (EmptyResourcePathsProvider.class) {
                if (instance == null) {
                    instance = new EmptyResourcePathsProvider();
                }
            }
        }
        return instance;
    }

    @Override
    public List<ResourcePath> getPaths(Resource<?, ?> resource) {
        return Collections.emptyList();
    }

}
