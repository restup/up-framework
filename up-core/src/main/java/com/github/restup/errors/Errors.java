package com.github.restup.errors;

import java.util.List;

/**
 * Collects errors during request handling
 */
public interface Errors {

    /**
     * @return All added errors
     */
    List<RequestError> getErrors();

    /**
     * Add an error to the list of errors.
     *
     * @param error
     */
    void addError(ErrorBuilder error);

    /**
     * @throws ErrorObjectException if 1 or more errors exist
     */
    void assertErrors() throws ErrorObjectException;

    /**
     * @return true if 1 or more errors exist, false otherwise
     */
    boolean hasErrors();

}
