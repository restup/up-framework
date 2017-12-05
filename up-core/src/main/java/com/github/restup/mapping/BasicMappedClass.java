package com.github.restup.mapping;

import static com.github.restup.util.UpUtils.unmodifiableList;

import java.util.List;
import java.util.Objects;

import com.github.restup.mapping.fields.MappedField;

/**
 * Provides an api to object to persistence mapping.
 *
 * @author andy.buttaro
 */
public class BasicMappedClass<T> implements MappedClass<T> {

    private final String name;
    private final String pluralName;
    private final Class<T> type;
    private final Class<?> parentType;
    private final List<MappedField<?>> attributes;

    protected BasicMappedClass(String name, String pluralName, Class<T> type, Class<?> parentType, List<MappedField<?>> attributes) {
        this.name = name;
        this.pluralName = pluralName;
        this.type = type;
        this.parentType = parentType;
        this.attributes = unmodifiableList(attributes);
    }

    /**
     * The name of the object
     */
    public String getName() {
        return name;
    }

    /**
     * The pluralized name of the object
     */
    public String getPluralName() {
        return pluralName;
    }

    /**
     * The type of the object
     */
    public Class<T> getType() {
        return type;
    }

    /**
     * The type of the object's parent
     */
    public Class<?> getParentType() {
        return parentType;
    }

    /**
     * The attributes of the object
     */

    public List<MappedField<?>> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BasicMappedClass<?> that = (BasicMappedClass<?>) o;
        return name == that.name &&
                type == that.type;
    }
}
