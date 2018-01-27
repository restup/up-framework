package com.github.restup.mapping.fields.composition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import com.github.restup.errors.RequestError;
import com.github.restup.mapping.fields.ReadWriteField;
import com.github.restup.util.Assert;

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

    @Override
    @SuppressWarnings("unchecked")
	public VALUE readValue(Object o) {
        try {
            return (VALUE) getter.invoke(o);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw RequestError.buildException(e);
        }
    }
    
    public Method getGetter() {
        return getter;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getter, getSetter());
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (! ( o instanceof ReflectMappedMethod )) {
            return false;
        }
        ReflectMappedMethod<?, ?> that = (ReflectMappedMethod<?, ?>) o;
        return Objects.equals(getter, that.getter)
                && Objects.equals(getSetter(), that.getSetter());
    }

}
