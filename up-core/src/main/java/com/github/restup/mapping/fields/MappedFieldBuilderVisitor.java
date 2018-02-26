package com.github.restup.mapping.fields;

import java.util.ArrayList;
import java.util.List;
import com.github.restup.mapping.fields.MappedField.Builder;
import com.github.restup.mapping.fields.visitors.IdentityByConventionMappedFieldBuilderVisitor;
import com.github.restup.mapping.fields.visitors.JacksonMappedFieldBuilderVisitor;
import com.github.restup.registry.settings.AutoDetectConstants;
import com.github.restup.util.ReflectionUtils.BeanInfo;
import com.github.restup.util.ReflectionUtils.PropertyDescriptor;

/**
 * Allows visiting a builder while building a {@link MappedField} using {@link DefaultMappedFieldFactory} for handling api, persistence, or other custom annotations.
 *
 * @author abuttaro
 */
public interface MappedFieldBuilderVisitor {

    /**
     * Visit a {@link MappedField.Builder} during field mapping to provide customization of field attributes
     *
     * @param b builder to visit
     * @param bi describing the Class being mapped
     * @param pd describing the property being mapped
     * @param <T> type of class being mapped
     */
    <T> void visit(Builder<T> b, BeanInfo<T> bi, PropertyDescriptor pd);

    static MappedFieldBuilderVisitor[] getDefaultVisitors() {
        List<MappedFieldBuilderVisitor> visitors = new ArrayList<MappedFieldBuilderVisitor>();
        visitors.add(new IdentityByConventionMappedFieldBuilderVisitor());
        if (AutoDetectConstants.JACKSON2_EXISTS) {
            visitors.add(new JacksonMappedFieldBuilderVisitor());
        }
        return visitors.toArray(new MappedFieldBuilderVisitor[0]);
    }

}
