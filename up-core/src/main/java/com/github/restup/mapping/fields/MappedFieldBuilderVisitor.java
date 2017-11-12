package com.github.restup.mapping.fields;

import com.github.restup.util.ReflectionUtils.BeanInfo;
import com.github.restup.util.ReflectionUtils.PropertyDescriptor;

/**
 * Allows visiting a builder while building a {@link MappedField} using
 * {@link DefaultMappedFieldFactory} for handling api, persistence, or
 * other custom annotations.
 *
 * @author abuttaro
 */
public interface MappedFieldBuilderVisitor {

    /**
     * Visit a {@link MappedField.Builder} during field mapping to provide customization
     * of field attributes
     *
     * @param b   builder to visit
     * @param bi  describing the Class being mapped
     * @param pd  describing the property being mapped
     * @param <T> type of class being mapped
     */
    <T> void visit(MappedField.Builder<T> b, BeanInfo<T> bi, PropertyDescriptor pd);

}
