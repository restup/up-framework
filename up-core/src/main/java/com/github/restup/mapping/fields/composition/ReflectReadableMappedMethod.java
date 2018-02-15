package com.github.restup.mapping.fields.composition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import com.github.restup.errors.RequestErrorException;
import com.github.restup.mapping.fields.DeclaredBy;
import com.github.restup.mapping.fields.ReadableField;
import com.github.restup.util.Assert;

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

    public static ReflectReadableMappedMethod<Object> of(Method getter) {
        return new ReflectReadableMappedMethod<>(getter);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T readValue(Object o) {
        try {
            return (T) getter.invoke(o);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw RequestErrorException.of(e);
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
    public final int hashCode() {
        return Objects.hash(getter);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (! ( o instanceof ReflectReadableMappedMethod )) {
            return false;
        }
        ReflectReadableMappedMethod<?> that = (ReflectReadableMappedMethod<?>) o;
        return Objects.equals(getter, that.getter);
    }

}
