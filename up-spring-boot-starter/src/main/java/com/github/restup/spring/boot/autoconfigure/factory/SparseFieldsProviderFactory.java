package com.github.restup.spring.boot.autoconfigure.factory;

import com.github.restup.path.ResourcePathsProvider;

public interface SparseFieldsProviderFactory {

    default ResourcePathsProvider getSparseFieldsProvider() {
        return null;
    }

}
