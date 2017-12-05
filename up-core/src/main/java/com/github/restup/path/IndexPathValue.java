package com.github.restup.path;

import com.github.restup.mapping.fields.ReadableField;
import com.github.restup.mapping.fields.WritableField;
import com.github.restup.util.ReflectionUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

/**
 * {@link PathValue} indicating the index of a Collection or Array.
 */
public class IndexPathValue implements PathValue, ReadableField<Object>, WritableField<Object, Object> {

    private final int index;
    private final Class<?> type;

    /**
     * {@link #toString()}
     *
     * @param type the expected type
     * @param index the index for this {@link PathValue}
     */
    public IndexPathValue(Class<?> type, int index) {
        this.index = index;
        this.type = type;
    }

    public IndexPathValue(int index) {
        this(ArrayList.class, index);
    }

    @Override
    public boolean isReservedPath() {
        return false;
    }

    /**
     * @return a new instance of {@link #type}
     */
    public Object createDeclaringInstance() {
        return ReflectionUtils.newInstance(type);
    }

    @Override
    public Object createInstance() {
        return null;
    }

    /**
     * @return {@link #toString()}
     */
    public String getApiPath() {
        return toString();
    }

    /**
     * @return {@link #toString()}
     */
    public String getBeanPath() {
        return toString();
    }

    /**
     * @return {@link #toString()}
     */
    public String getPersistedPath() {
        return toString();
    }

    /**
     * @return the string value of {@link #index}
     */
    @Override
    public String toString() {
        return String.valueOf(index);
    }

    /**
     * @return true for Collection implementations or Object[], false otherwise
     */
    public boolean supportsType(Class<?> clazz) {
        return Collection.class.isAssignableFrom(clazz)
                || Object[].class == clazz;
    }

    public Object readValue(Object instance) {
        if (instance instanceof Collection) {
            return CollectionUtils.get(instance, index);
        } else if (instance instanceof Object[]) {
            Object[] arr = (Object[]) instance;
            return arr[index];
        } else if (instance != null) {
            throw new IllegalArgumentException(instance.getClass() + "not supported");
        }
        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void writeValue(Object instance, Object value) {
        if (instance instanceof List) {
            ((List) instance).set(index, value);
        } else if (instance instanceof Object[]) {
            Object[] arr = (Object[]) instance;
            arr[index] = value;
        } else if (instance != null) {
            throw new IllegalArgumentException(instance.getClass() + "not supported");
        }
    }

    @Override
    public int hashCode() {
        return index;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        IndexPathValue other = (IndexPathValue) obj;
        if (index != other.index) {
            return false;
        }
        return true;
    }

}
