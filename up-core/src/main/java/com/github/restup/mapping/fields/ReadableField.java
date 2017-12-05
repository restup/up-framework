package com.github.restup.mapping.fields;

/**
 * A field which may be read
 */
public interface ReadableField<T> {

    /**
     * Read a property's value
     *
     * @param instance of the object whose property to read
     * @return the value of the field
     */
    T readValue(Object instance);

}
