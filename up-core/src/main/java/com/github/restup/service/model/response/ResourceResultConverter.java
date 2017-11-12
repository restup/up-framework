package com.github.restup.service.model.response;

/**
 * Converts a resource result.
 *
 * Allows annotated methods to return any type and automatically
 * wrap with Up! response types
 */
public interface ResourceResultConverter {

    Object convert(Object o);

}
