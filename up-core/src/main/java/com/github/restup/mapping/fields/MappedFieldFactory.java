package com.github.restup.mapping.fields;

import com.github.restup.util.ReflectionUtils.BeanInfo;
import com.github.restup.util.ReflectionUtils.PropertyDescriptor;

/**
 * A factory for building {@link MappedField} instances
 */
public interface MappedFieldFactory {

    /**
     * @param bi  describing the object being mapped
     * @param pd  describing the property being mapped
     * @param <T> the type of the class being mapped
     * @return a {@link MappedField} instance describing the property
     */
    <T> MappedField<T> getMappedField(BeanInfo<T> bi, PropertyDescriptor pd);

}
