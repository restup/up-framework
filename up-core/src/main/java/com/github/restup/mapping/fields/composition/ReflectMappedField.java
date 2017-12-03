package com.github.restup.mapping.fields.composition;

import java.lang.reflect.Field;
import java.util.Objects;

import com.github.restup.errors.ErrorBuilder;
import com.github.restup.mapping.fields.ReadWriteField;
import com.github.restup.util.ReflectionUtils;

public class ReflectMappedField<TARGET, VALUE> implements ReadWriteField<TARGET, VALUE> {

    private final Field field;

    ReflectMappedField(Field field) {
    		field.setAccessible(true);
        this.field = field;
    }

    public static ReflectMappedField<?,?> of(Field field) {
        return new ReflectMappedField<>(field);
    }

    @Override
    public Object readValue(Object o) {
        try {
            return o == null ? null : field.get(o);
        } catch (IllegalAccessException e) {
            throw ErrorBuilder.buildException(e);
        } catch (IllegalArgumentException e) {
            throw ErrorBuilder.buildException(e);
        }
    }

    @Override
    public void writeValue(TARGET obj, VALUE value) {
        if (obj != null) {
            try {
                field.set(obj, value);
            } catch (IllegalAccessException e) {
                ErrorBuilder.throwError(e);
            } catch (IllegalArgumentException e) {
                ErrorBuilder.throwError(e);
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
    public int hashCode() {
        return Objects.hash(field);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReflectMappedField<?,?> that = (ReflectMappedField<?,?>) o;
        return Objects.equals(field, that.field);
    }

}
