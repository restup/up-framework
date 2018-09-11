package com.github.restup.service.model.response;

import com.github.restup.registry.Resource;
import java.io.Serializable;
import java.util.List;

class BasicRelatedResourceResult<T, ID extends Serializable> implements
    RelatedResourceResult<T, ID> {

    private final Resource<T, ID> resource;
    private final ReadResult<List<T>> result;

    BasicRelatedResourceResult(Resource<T, ID> resource,
        ReadResult<List<T>> result) {
        this.resource = resource;
        this.result = result;
    }

    @Override
    public ReadResult<List<T>> getResult() {
        return result;
    }

    @Override
    public Resource<T, ID> getResource() {
        return resource;
    }

}
