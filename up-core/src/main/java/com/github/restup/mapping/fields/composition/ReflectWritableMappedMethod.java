package com.github.restup.mapping.fields.composition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import com.github.restup.errors.RequestErrorException;
import com.github.restup.mapping.fields.DeclaredBy;
import com.github.restup.mapping.fields.WritableField;
import com.github.restup.util.Assert;
import com.github.restup.util.ReflectionUtils;

/**
 * For write only property mapped by Method
 *
 * @author abuttaro
 */
public class ReflectWritableMappedMethod<TARGET, VALUE> implements WritableField<TARGET, VALUE>, DeclaredBy {

    private final Method setter;

    ReflectWritableMappedMethod(Method setter) {
        Assert.notNull(setter, "setter is required");
        this.setter = setter;
    }

    public static ReflectWritableMappedMethod<?, ?> of(Method setter) {
        return new ReflectWritableMappedMethod<>(setter);
    }

    @Override
    public void writeValue(TARGET obj, VALUE value) {
        if (obj != null) {
            try {
                setter.invoke(obj, value);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                RequestErrorException.rethrow(e);
            }
        }
    }

    @Override
    public boolean isDeclaredBy(Class<?> clazz) {
        return setter.getDeclaringClass() == clazz;
    }

    @Override
    public TARGET createDeclaringInstance() {
        return createDeclaringInstance(setter);
    }

    @SuppressWarnings("unchecked")
    public TARGET createDeclaringInstance(Method m) {
        return (TARGET) ReflectionUtils.newInstance(m.getDeclaringClass());
    }

    public Method getSetter() {
        return setter;
    }

    @Override
    public int hashCode() {
        return Objects.hash(setter);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReflectWritableMappedMethod<?, ?> that = (ReflectWritableMappedMethod<?, ?>) o;
        return Objects.equals(setter, that.setter);
    }

}
