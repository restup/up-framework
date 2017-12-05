package com.github.restup.mapping.fields;

/**
 * {@link MappedField} representing an {@link Iterable} type, capturing detail of the generic type of items
 */
public interface IterableField<T> extends MappedField<T> {

    Class<?> getGenericType();

}
