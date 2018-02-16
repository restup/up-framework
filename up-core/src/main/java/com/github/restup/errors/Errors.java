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
     * @param error to add
     */
    void addError(RequestError.Builder error);

    /**
     * @throws RequestErrorException if 1 or more errors exist
     */
    void assertErrors() throws RequestErrorException;

    /**
     * @return true if 1 or more errors exist, false otherwise
     */
    boolean hasErrors();

}
