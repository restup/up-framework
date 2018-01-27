package com.github.restup.errors;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import java.util.ArrayList;
import java.util.List;

/**
 * Default {@link Errors} implementation
 */
class BasicErrors implements Errors {

    private List<RequestError> errors;

    BasicErrors() {
        super();
    }

    @Override
    public List<RequestError> getErrors() {
        return errors;
    }

    public void setErrors(List<RequestError> errors) {
        this.errors = errors;
    }

    @Override
    public void addError(RequestError.Builder error) {
        if (error != null) {
            addError(error.build());
        }
    }

    public void addError(RequestError error) {
        if (error != null) {
            List<RequestError> errors = getErrors();
            if (errors == null) {
                errors = new ArrayList<RequestError>();
                setErrors(errors);
            }
            errors.add(error);
        }
    }

    @Override
    public void assertErrors() {
        if (hasErrors()) {
            throw new ErrorObjectException(getErrors());
        }
    }

    @Override
    public boolean hasErrors() {
        return isNotEmpty(getErrors());
    }

}
