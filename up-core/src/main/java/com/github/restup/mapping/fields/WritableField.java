package com.github.restup.mapping.fields;

/**
 * A field which is modifiable
 */
public interface WritableField<TARGET, VALUE> {

    /**
     * Set the the field's value on the instance provided
     *
     * @param instance of the object whose property to set
     * @param value the value to set
     */
    void writeValue(TARGET instance, VALUE value);

    /**
     * Returns a new instance of the object containing the field so that it may be populated using {@link #writeValue(Object, Object)} if needed
     */
    TARGET createDeclaringInstance();

    VALUE createInstance();

}
