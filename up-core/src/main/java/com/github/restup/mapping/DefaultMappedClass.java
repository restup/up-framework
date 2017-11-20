package com.github.restup.mapping;

import com.github.restup.mapping.fields.MappedField;

import java.util.List;

import static com.github.restup.util.UpUtils.unmodifiableList;

/**
 * Default {@link MappedClass} implementation.
 *
 * @param <T>
 */
public class DefaultMappedClass<T> implements MappedClass<T> {

    private final String name;
    private final String pluralName;
    private final Class<T> type;
    private final Class<?> parentType;
    private final List<MappedField<?>> attributes;

    public DefaultMappedClass(String name, String pluralName, Class<T> type, Class<?> parentType, List<MappedField<?>> attributes) {
        this.name = name;
        this.pluralName = pluralName;
        this.type = type;
        this.parentType = parentType;
        this.attributes = unmodifiableList(attributes);
    }

    public String getName() {
        return name;
    }

    public String getPluralName() {
        return pluralName;
    }

    public Class<T> getType() {
        return type;
    }

    public Class<?> getParentType() {
        return parentType;
    }

    public List<MappedField<?>> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return name;
    }
}
