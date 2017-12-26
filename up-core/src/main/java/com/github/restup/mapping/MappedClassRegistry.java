package com.github.restup.mapping;

import java.lang.reflect.Type;

public interface MappedClassRegistry {


    /**
     * @return an instance of MappedClass describing clazz, or null if {@link #isMappable(Class)} returns false
     */
    MappedClass<?> getMappedClass(Type type);

	@SuppressWarnings("unchecked")
	default <T> MappedClass<T> getMappedClass(Class<T> resourceClass) {
        return (MappedClass<T>) getMappedClass((Type) resourceClass);
    }
    
}
