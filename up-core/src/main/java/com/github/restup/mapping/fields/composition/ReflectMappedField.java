package com.github.restup.mapping.fields.composition;

import java.lang.reflect.Field;
import java.util.Objects;
import com.github.restup.errors.RequestErrorException;
import com.github.restup.mapping.fields.ReadWriteField;
import com.github.restup.util.ReflectionUtils;

public class ReflectMappedField<TARGET, VALUE> implements ReadWriteField<TARGET, VALUE> {

    private final Field field;

    ReflectMappedField(Field field) {
        field.setAccessible(true);
        this.field = field;
    }

    public static ReflectMappedField<?, ?> of(Field field) {
        return new ReflectMappedField<>(field);
    }

    @SuppressWarnings("unchecked")
    @Override
    public VALUE readValue(Object o) {
        try {
            return o == null ? null : (VALUE) field.get(o);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw RequestErrorException.of(e);
        }
    }

    @Override
    public void writeValue(TARGET obj, VALUE value) {
        if (obj != null) {
            try {
                field.set(obj, value);
            } catch (IllegalAccessException | IllegalArgumentException e) {
                RequestErrorException.rethrow(e);
            }
        }
    }

    public Field getField() {
        return field;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TARGET createDeclaringInstance() {
        return (TARGET) ReflectionUtils.newInstance(field.getDeclaringClass());
    }

    @SuppressWarnings("unchecked")
    @Override
    public VALUE createInstance() {
        return (VALUE) ReflectionUtils.newInstance(field.getType());
    }

    @Override
    public boolean isDeclaredBy(Class<?> clazz) {
        return field.getDeclaringClass() == clazz;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(field);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReflectMappedField)) {
            return false;
        }
        ReflectMappedField<?, ?> that = (ReflectMappedField<?, ?>) o;
        return Objects.equals(field, that.field);
    }

}
