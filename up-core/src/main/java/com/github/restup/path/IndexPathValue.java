package com.github.restup.path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;
import com.github.restup.mapping.fields.ReadableField;
import com.github.restup.mapping.fields.WritableField;
import com.github.restup.util.ReflectionUtils;

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

    /**
     * @return a new instance of {@link #type}
     */
    @Override
    public Object createDeclaringInstance() {
        return ReflectionUtils.newInstance(type);
    }

    /**
     * @return {@link #toString()}
     */
    @Override
    public String getApiPath() {
        return toString();
    }

    /**
     * @return {@link #toString()}
     */
    @Override
    public String getBeanPath() {
        return toString();
    }

    /**
     * @return {@link #toString()}
     */
    @Override
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
    @Override
    public boolean supportsType(Class<?> clazz) {
        return Collection.class.isAssignableFrom(clazz)
                || Object[].class == clazz;
    }

    @Override
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

    @Override
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

    public int getIndex() {
        return index;
    }

    public Class<?> getType() {
        return type;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(index, type);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (! ( o instanceof IndexPathValue )) {
            return false;
        }
        IndexPathValue that = (IndexPathValue) o;
        return Objects.equals(index, that.index)
                && Objects.equals(type, that.type);
    }

}
