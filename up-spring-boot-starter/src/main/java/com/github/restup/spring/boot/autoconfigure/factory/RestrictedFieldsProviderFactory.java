package com.github.restup.spring.boot.autoconfigure.factory;

import com.github.restup.path.ResourcePathsProvider;

public interface RestrictedFieldsProviderFactory {

    default ResourcePathsProvider getRestrictedFieldsProvider() {
        return null;
    }

}
