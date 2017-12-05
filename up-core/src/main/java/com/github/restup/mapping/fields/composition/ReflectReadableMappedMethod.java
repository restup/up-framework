package com.github.restup.mapping.fields.composition;

import com.github.restup.errors.ErrorBuilder;
import com.github.restup.mapping.fields.DeclaredBy;
import com.github.restup.mapping.fields.ReadableField;
import com.github.restup.util.Assert;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * For read only property mapped by Method
 *
 * @author abuttaro
 */
public class ReflectReadableMappedMethod<T> implements ReadableField<T>, DeclaredBy {

    private final Method getter;

    ReflectReadableMappedMethod(Method getter) {
        Assert.notNull(getter, "getter is required");
        this.getter = getter;
    }

    public static ReflectReadableMappedMethod<?> of(Method getter) {
        return new ReflectReadableMappedMethod<>(getter);
    }

    @SuppressWarnings("unchecked")
    public T readValue(Object o) {
        try {
            return (T) getter.invoke(o);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw ErrorBuilder.buildException(e);
        }
    }

    @Override
    public boolean isDeclaredBy(Class<?> clazz) {
        return getter.getDeclaringClass() == clazz;
    }

    public Method getGetter() {
        return getter;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getter);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReflectReadableMappedMethod<?> that = (ReflectReadableMappedMethod<?>) o;
        return Objects.equals(getter, that.getter);
    }

}
