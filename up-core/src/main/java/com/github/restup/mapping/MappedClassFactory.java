package com.github.restup.mapping;

/**
 * A factory for building {@link MappedClass} meta data
 */
public interface MappedClassFactory {

    /**
     * @param <T> type of {@link MappedClass}
     * @param clazz for which to obtain {@link MappedClass} instance
     * @return an instance of MappedClass describing clazz, or null if {@link #isMappable(Class)}
     *         returns false
     */
    <T> MappedClass<T> getMappedClass(Class<T> clazz);

    /**
     * @param type to check if is mappable
     * @return true if the factory supports mapping the type, false otherwise
     */
    boolean isMappable(Class<?> type);

}
