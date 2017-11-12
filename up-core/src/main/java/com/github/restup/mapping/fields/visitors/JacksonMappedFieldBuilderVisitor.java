package com.github.restup.mapping.fields.visitors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.mapping.fields.MappedFieldBuilderVisitor;
import com.github.restup.util.ReflectionUtils.BeanInfo;
import com.github.restup.util.ReflectionUtils.PropertyDescriptor;

import static com.github.restup.util.ReflectionUtils.getAnnotation;

/**
 * {@link MappedFieldBuilderVisitor} whic
 */
public class JacksonMappedFieldBuilderVisitor implements MappedFieldBuilderVisitor {

    /**
     * Checks for {@link JsonProperty} and applies the api property name to builder
     * if the annotation exists
     */
    public <T> void visit(MappedField.Builder<T> b, BeanInfo<T> bi, PropertyDescriptor pd) {
        JsonIgnore ignore = getAnnotation(JsonIgnore.class, pd);
        if (ignore != null) {
            b.setApiName(null);
        } else {
            JsonProperty annotation = getAnnotation(JsonProperty.class, pd);
            if (annotation != null) {
                b.setApiName(annotation.value());
            }
        }
    }

}
