package com.github.restup.path;

import com.github.restup.registry.Resource;

public class EmbeddedResourcePathValue implements PathValue {

    private final Resource<?, ?> resource;
    private final String path;

    public EmbeddedResourcePathValue(Resource<?, ?> resource, String path) {
        super();
        this.resource = resource;
        this.path = path;
    }

    @Override
    public String getApiPath() {
        return path;
    }

    @Override
    public String getBeanPath() {
        return null;
    }

    @Override
    public String getPersistedPath() {
        return null;
    }

    @Override
    public boolean supportsType(Class<?> instance) {
        return instance == resource.getType();
    }

    @Override
    public boolean isReservedPath() {
        return false;
    }

    public Resource<?, ?> getResource() {
        return resource;
    }

}
