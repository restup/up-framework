package com.github.restup.mapping;

import com.github.restup.mapping.fields.MappedField;

import java.util.List;

public abstract class PolymorphicMappedClass<T> extends BasicMappedClass<T> {

    protected PolymorphicMappedClass(String name, String pluralName, Class<T> type, Class<?> parentType, List<MappedField<?>> attributes) {
        super(name, pluralName, type, parentType, attributes);
    }

    public abstract List<Class<?>> getSubTypes();

}
