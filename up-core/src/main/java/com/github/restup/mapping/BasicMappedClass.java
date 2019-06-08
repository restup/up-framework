package com.github.restup.mapping;

import static com.github.restup.util.UpUtils.unmodifiableList;

import com.github.restup.mapping.fields.MappedField;
import com.github.restup.util.ReflectionUtils;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

/**
 * Provides an api to object to persistence mapping.
 *
 * @author andy.buttaro
 */
public class BasicMappedClass<T> implements MappedClass<T> {

    private final String name;
    private final String pluralName;
    private final String persistedName;
    private final Type type;
    private final Type parentType;
    private final List<MappedField<?>> attributes;
    private final boolean typedMapPresent;
    private final boolean indexedQueryOnly;

    protected BasicMappedClass(String name, String pluralName, String persistedName, Type type,
        Type parentType, List<MappedField<?>> attributes, boolean typedMapPresent,
        boolean indexedQueryOnly) {
        this.name = name;
        this.pluralName = pluralName;
        this.persistedName = persistedName;
        this.type = type;
        this.parentType = parentType;
        this.attributes = unmodifiableList(attributes);
        this.typedMapPresent = typedMapPresent;
        this.indexedQueryOnly = indexedQueryOnly;
    }
    
    @Override
    public T newInstance() {
	    	return ReflectionUtils.newInstance(type);
    }

    /**
     * The name of the object
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * The pluralized name of the object
     */
    @Override
    public String getPluralName() {
        return pluralName;
    }

    @Override
    public String getPersistedName() {
        return persistedName;
    }

    /**
     * The type of the object
     */
    @Override
    public Type getType() {
        return type;
    }

    /**
     * The type of the object's parent
     */
    @Override
    public Type getParentType() {
        return parentType;
    }

    /**
     * The attributes of the object
     */

    @Override
    public List<MappedField<?>> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(name, type);
    }
    
    @Override
    public boolean isTypedMapPresent() {
    		return typedMapPresent;
    }

    @Override
    public boolean isIndexedQueryOnly() {
        return indexedQueryOnly;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ( ! (o instanceof BasicMappedClass) ) {
            return false;
        }
        BasicMappedClass<?> that = (BasicMappedClass<?>) o;
        return Objects.equals(name, that.name) &&
                type == that.type;
    }
}
