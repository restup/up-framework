package com.github.restup.mapping;

import java.lang.reflect.Type;

public interface MappedClassRegistry {


    /**
     * @param type of {@link MappedClass} to get
     * @return an instance of MappedClass describing type, or null if type is not mappable
     */
    MappedClass<?> getMappedClass(Type type);

    /**
     * @param <T> type of {@link MappedClass}
     * @param resourceClass type of class
     * @return mapped class instance
     */
    @SuppressWarnings("unchecked")
	default <T> MappedClass<T> getMappedClass(Class<T> resourceClass) {
        return (MappedClass<T>) getMappedClass((Type) resourceClass);
    }
    
}
