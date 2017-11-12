package com.github.restup.errors;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

/**
 * Default {@link Errors} implementation
 */
class DefaultErrors implements Errors {

    private List<RequestError> errors;

    DefaultErrors() {
        super();
    }

    public List<RequestError> getErrors() {
        return errors;
    }

    public void setErrors(List<RequestError> errors) {
        this.errors = errors;
    }

    public void addError(ErrorBuilder error) {
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

    public void assertErrors() {
        if (hasErrors()) {
            throw new ErrorObjectException(getErrors());
        }
    }

    public boolean hasErrors() {
        return isNotEmpty(getErrors());
    }

}
