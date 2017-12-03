package com.github.restup.path;

import java.util.Objects;

import com.github.restup.mapping.fields.MappedField;
import com.github.restup.mapping.fields.ReadableField;
import com.github.restup.mapping.fields.WritableField;
import com.github.restup.util.Assert;

public class MappedFieldPathValue<T> implements PathValue, ReadableField, WritableField<Object, T> {

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

    public String getBeanPath() {
        return mappedField.getBeanName();
    }

    public String getApiPath() {
        return mappedField.getApiName();
    }

    public String getPersistedPath() {
        return mappedField.getPersistedName();
    }

    @Override
    public String toString() {
        return mappedField.toString();
    }

    public Object readValue(Object instance) {
        return mappedField.readValue(instance);
    }

    public void writeValue(Object instance, T value) {
        mappedField.writeValue(instance, value);
    }

    public Object createDeclaringInstance() {
        return mappedField.createDeclaringInstance();
    }
    
    @Override
    public T createInstance() {
    		return mappedField.createInstance();
    }

    public boolean supportsType(Class<?> clazz) {
        return mappedField.isDeclaredBy(clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mappedField);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MappedFieldPathValue other = (MappedFieldPathValue) obj;
        if (mappedField == null) {
            if (other.mappedField != null)
                return false;
        } else if (!mappedField.equals(other.mappedField))
            return false;
        return true;
    }

}
