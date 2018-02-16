package com.github.restup.service.filters;

import java.io.Serializable;
import com.github.restup.annotations.filter.PostReadFilter;
import com.github.restup.errors.RequestError;
import com.github.restup.errors.StatusCode;
import com.github.restup.registry.Resource;
import com.github.restup.service.model.request.ReadRequest;
import com.github.restup.service.model.response.ReadResult;

/**
 * Provides post filter for resource not found errors
 */
public class NotFoundFilter {

    /**
     * Requires ResourceRepository to return a {@link ReadResult}
     * 
     * @param <T> resource type
     * @param <ID> resource id type
     * @param resource requested
     * @param request object
     * @param result object
     */
    @PostReadFilter
    public <T, ID extends Serializable> void assertResourceNotFound(Resource<T, ID> resource, ReadRequest<T, ID> request, ReadResult<T> result) {
        if (result == null || result.getData() == null) {
            RequestError.builder().resource(resource)
                    .status(StatusCode.NOT_FOUND)
                    .code("RESOURCE_NOT_FOUND")
                    .detail("Resource not found")
                    .meta("id", request.getId())
                    .throwError();
        }
    }

}
