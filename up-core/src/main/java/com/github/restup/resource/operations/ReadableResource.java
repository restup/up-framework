package com.github.restup.resource.operations;

import com.github.restup.annotations.operations.ReadResource;
import com.github.restup.service.model.request.ReadRequest;
import com.github.restup.service.model.response.ReadResult;

import java.io.Serializable;

public interface ReadableResource<T, ID extends Serializable> {

    @ReadResource
    ReadResult<T> find(ReadRequest<T, ID> request);

}
