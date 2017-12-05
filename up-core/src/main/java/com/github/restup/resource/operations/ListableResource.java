package com.github.restup.resource.operations;

import com.github.restup.annotations.operations.ListResource;
import com.github.restup.service.model.request.ListRequest;
import com.github.restup.service.model.response.ReadResult;
import java.util.List;

public interface ListableResource<T> {

    @ListResource
    ReadResult<List<T>> list(ListRequest<T> request);

}
