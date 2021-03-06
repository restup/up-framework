package com.github.restup.service.filters;

import java.io.Serializable;
import com.github.restup.annotations.filter.PreCreateFilter;
import com.github.restup.errors.ErrorCode;
import com.github.restup.errors.Errors;
import com.github.restup.errors.RequestError;
import com.github.restup.errors.StatusCode;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.registry.Resource;
import com.github.restup.service.ServiceFilter;
import com.github.restup.service.model.request.CreateRequest;

public class SequencedIdValidationFilter implements ServiceFilter {

    /**
     * Only applied when identity field does not permit auto generated values
     * 
     * @param resource of request
     * @param <T> resource type
     * @param <ID> resource id type
     */
    @Override
    public <T, ID extends Serializable> boolean accepts(Resource<T, ID> resource) {
        return !resource.getIdentityField().isIdentifierNonAutoGeneratedValuePermitted();
    }

    /**
     * If an id has a non null value, add an error to errors
     * 
     * @param errors to collect any errors found
     * @param resource of request
     * @param request object
     * @param <T> resource type
     * @param <ID> resource id type
     */
    @PreCreateFilter
    public <T, ID extends Serializable> void validateIdNotPresent(Errors errors, Resource<T, ID> resource, CreateRequest<T> request) {
        MappedField<ID> idField = resource.getIdentityField();
        ID id = idField.readValue(request.getData());
        if (id != null) {
            errors.addError(RequestError.builder(resource)
                    .code(ErrorCode.ID_NOT_ALLOWED_ON_CREATE)
                    .status(StatusCode.FORBIDDEN)
                    .meta(idField.getApiName(), id));
        }
    }

}
