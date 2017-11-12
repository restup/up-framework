package com.github.restup.service.filters;

import com.github.restup.annotations.filter.PostReadFilter;
import com.github.restup.errors.ErrorBuilder;
import com.github.restup.registry.Resource;
import com.github.restup.service.model.request.ReadRequest;
import com.github.restup.service.model.response.ReadResult;

import java.io.Serializable;

/**
 * Provides post filter for resource not found errors
 */
public class NotFoundFilter {

    /**
     * Requires ResourceRepository to return a {@link ReadResult}
     *
     * @param resource
     * @param request
     * @param result
     */
    @PostReadFilter
    public <T, ID extends Serializable> void assertResourceNotFound(Resource<T, ID> resource, ReadRequest<T, ID> request, ReadResult<T> result) {
        if (result != null && result.getData() == null) {
            ErrorBuilder.builder().resource(resource)
                    .status(ErrorBuilder.ErrorCodeStatus.NOT_FOUND)
                    .code("RESOURCE_NOT_FOUND")
                    .detail("Resource not found")
                    .meta("id", request.getId())
                    .throwError();
        }
    }

}
