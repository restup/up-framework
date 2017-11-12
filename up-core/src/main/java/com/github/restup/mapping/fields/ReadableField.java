package com.github.restup.mapping.fields;

/**
 * A field which may be read
 *
 * @param <T>
 */
public interface ReadableField<T> {

    /**
     * Read a property's value
     *
     * @param instance of the object whose property to read
     * @return the value of the field
     */
    Object readValue(Object instance);

}
