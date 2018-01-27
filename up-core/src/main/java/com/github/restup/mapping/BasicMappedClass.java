package com.github.restup.mapping;

import static com.github.restup.util.UpUtils.unmodifiableList;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.util.ReflectionUtils;

/**
 * Provides an api to object to persistence mapping.
 *
 * @author andy.buttaro
 */
public class BasicMappedClass<T> implements MappedClass<T> {

    private final String name;
    private final String pluralName;
    private final Type type;
    private final Type parentType;
    private final List<MappedField<?>> attributes;
    private final boolean typedMapPresent;

    protected BasicMappedClass(String name, String pluralName, Type type, Type parentType, List<MappedField<?>> attributes, boolean typedMapPresent) {
        this.name = name;
        this.pluralName = pluralName;
        this.type = type;
        this.parentType = parentType;
        this.attributes = unmodifiableList(attributes);
        this.typedMapPresent = typedMapPresent;
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
