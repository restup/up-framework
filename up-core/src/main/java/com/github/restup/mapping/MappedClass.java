package com.github.restup.mapping;

import com.github.restup.mapping.fields.MappedField;

import java.util.List;

/**
 * Provides an api to object to persistence mapping.
 *
 * @param <T>
 * @author andy.buttaro
 */
public interface MappedClass<T> {

    /**
     * The name of the object
     *
     * @return
     */
    String getName();

    /**
     * The pluralized name of the object
     *
     * @return
     */
    String getPluralName();

    /**
     * The type of the object
     *
     * @return
     */
    Class<T> getType();

    /**
     * The type of the object's parrent
     *
     * @return
     */
    Class<?> getParentType();

    /**
     * The attributes of the object
     *
     * @return
     */
    List<MappedField<?>> getAttributes();

}
