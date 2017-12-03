package com.github.restup.mapping.fields;

import java.io.Serializable;

/**
 * {@link MappedField} representing an {@link Iterable} type, capturing detail of the generic type of items
 *
 * @param <ID>
 */
public interface IterableField<ID extends Serializable> extends MappedField<ID> {

    Class<?> getGenericType();

}
