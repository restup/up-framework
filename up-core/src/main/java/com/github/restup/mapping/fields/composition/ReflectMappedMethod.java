package com.github.restup.mapping.fields.composition;

import com.github.restup.errors.ErrorBuilder;
import com.github.restup.mapping.fields.ReadWriteField;
import com.github.restup.util.Assert;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class ReflectMappedMethod<TARGET, VALUE> extends ReflectWritableMappedMethod<TARGET, VALUE> implements ReadWriteField<TARGET, VALUE> {

    private final Method getter;

    ReflectMappedMethod(Method getter, Method setter) {
        super(setter);
        Assert.notNull(getter, "getter is required");
//    		TODO check consistency that they are of the same inheritance... not necessarily same class
//    		if ( getter.getDeclaringClass() != setter.getDeclaringClass() ) {
//    			throw new IllegalStateException("Getter and setter must be declared by same class");
//    		}
        this.getter = getter;
    }

    public static ReflectMappedMethod<?, ?> of(Method getter, Method setter) {
        return new ReflectMappedMethod<>(getter, setter);
    }

    @SuppressWarnings("unchecked")
	public VALUE readValue(Object o) {
        try {
            return (VALUE) getter.invoke(o);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw ErrorBuilder.buildException(e);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(getter, getSetter());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReflectMappedMethod<?, ?> that = (ReflectMappedMethod<?, ?>) o;
        return Objects.equals(getter, that.getter)
                && Objects.equals(getSetter(), that.getSetter());
    }

}
