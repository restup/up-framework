package com.github.restup.mapping.fields;

/**
 * A field which is modifiable
 *
 * @param <T>
 */
public interface WritableField<T> {

    /**
     * Set the the field's value on the instance provided
     *
     * @param instance of the object whose property to set
     * @param value    the value to set
     */
    void writeValue(Object instance, T value);

    /**
     * Returns a new instance of the object containing the field so that it may be
     * populated using {@link #writeValue(Object, Object)} if needed
     *
     * @return
     */
    Object getFieldInstance();
}
