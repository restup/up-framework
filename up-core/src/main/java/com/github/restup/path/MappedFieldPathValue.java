package com.github.restup.path;

import com.github.restup.mapping.fields.IdentityField;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.mapping.fields.ReadableField;
import com.github.restup.mapping.fields.WritableField;
import com.github.restup.util.Assert;

public class MappedFieldPathValue<T> implements PathValue, ReadableField<T>, WritableField<T> {

    private final MappedField<T> mappedField;

    public MappedFieldPathValue(MappedField<T> mappedField) {
        super();
        Assert.notNull(mappedField, "mappedField is required");
        this.mappedField = mappedField;
    }


    public static boolean isRelationshipField(PathValue pv) {
        if (pv instanceof MappedFieldPathValue) {
            return isRelationshipField((MappedFieldPathValue) pv);
        }
        return false;
    }


    public static boolean isRelationshipField(MappedFieldPathValue pv) {
        if (pv != null) {
            MappedField mf = pv.getMappedField();
            return mf.getRelationshipType() != null;
        }
        return false;
    }

    public MappedField<T> getMappedField() {
        return mappedField;
    }

    @Override
    public boolean isReservedPath() {
        return mappedField instanceof IdentityField;
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

    public Object getFieldInstance() {
        return mappedField.getFieldInstance();
    }

    public boolean supportsType(Class<?> clazz) {
        return clazz != null && mappedField.getDeclaringClass() == clazz;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mappedField == null) ? 0 : mappedField.hashCode());
        return result;
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
