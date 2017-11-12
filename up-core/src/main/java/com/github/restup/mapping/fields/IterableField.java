package com.github.restup.mapping.fields;

import com.github.restup.annotations.field.CaseInsensitive;
import com.github.restup.annotations.field.Immutable;
import com.github.restup.annotations.field.Param;
import com.github.restup.annotations.field.Relationship;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * {@link MappedField} representing an {@link Iterable} type, capturing detail of the generic type of items
 *
 * @param <ID>
 */
public class IterableField<ID extends Serializable> extends MappedField<ID> {

    private final Class<?> genericType;

    protected IterableField(Class<ID> type, String beanName, String apiName, String persistedName, boolean ignoreUpdateAttempt, boolean apiProperty, boolean transientField, CaseInsensitive caseInsensitive, Relationship relationship, Immutable immutable, Param param, Field field, Class<?> genericType) {
        super(type, beanName, apiName, persistedName, ignoreUpdateAttempt, apiProperty, transientField, caseInsensitive, relationship, immutable, param, field);
        this.genericType = genericType;
    }

    public Class<?> getGenericType() {
        return genericType;
    }

}
