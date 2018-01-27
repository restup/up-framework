package com.github.restup.path;

import java.util.Objects;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.mapping.fields.ReadableField;
import com.github.restup.mapping.fields.WritableField;
import com.github.restup.util.Assert;

public class MappedFieldPathValue<T> implements PathValue, ReadableField<T>, WritableField<Object, T> {

    private final MappedField<T> mappedField;

    public MappedFieldPathValue(MappedField<T> mappedField) {
        super();
        Assert.notNull(mappedField, "mappedField is required");
        this.mappedField = mappedField;
    }

    public static boolean isRelationshipField(PathValue pv) {
        if (pv instanceof MappedFieldPathValue) {
            return isRelationshipField((MappedFieldPathValue<?>) pv);
        }
        return false;
    }

    public static boolean isRelationshipField(MappedFieldPathValue<?> pv) {
        if (pv != null) {
            MappedField<?> mf = pv.getMappedField();
            return mf.isRelationship();
        }
        return false;
    }

    public MappedField<T> getMappedField() {
        return mappedField;
    }

    @Override
    public boolean isReservedPath() {
        return mappedField.isIdentifier();
    }

    @Override
    public String getBeanPath() {
        return mappedField.getBeanName();
    }

    @Override
    public String getApiPath() {
        return mappedField.getApiName();
    }

    @Override
    public String getPersistedPath() {
        return mappedField.getPersistedName();
    }

    @Override
    public String toString() {
        return mappedField.toString();
    }

    @Override
    public T readValue(Object instance) {
        return mappedField.readValue(instance);
    }

    @Override
    public void writeValue(Object instance, T value) {
        mappedField.writeValue(instance, value);
    }

    @Override
    public Object createDeclaringInstance() {
        return mappedField.createDeclaringInstance();
    }

    @Override
    public T createInstance() {
        return mappedField.createInstance();
    }

    @Override
    public boolean supportsType(Class<?> clazz) {
        return mappedField.isDeclaredBy(clazz);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(mappedField);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (! ( o instanceof MappedFieldPathValue )) {
            return false;
        }
        MappedFieldPathValue that = (MappedFieldPathValue) o;
        return Objects.equals(mappedField, that.mappedField);
    }

}
