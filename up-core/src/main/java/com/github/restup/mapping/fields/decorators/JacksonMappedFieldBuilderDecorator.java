package com.github.restup.mapping.fields.decorators;

import static com.github.restup.util.ReflectionUtils.getAnnotation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.mapping.fields.MappedFieldBuilderDecorator;
import com.github.restup.util.ReflectionUtils.BeanInfo;
import com.github.restup.util.ReflectionUtils.PropertyDescriptor;

/**
 * {@link MappedFieldBuilderDecorator} whic
 */
public class JacksonMappedFieldBuilderDecorator implements MappedFieldBuilderDecorator {

    /**
     * Checks for {@link JsonProperty} and applies the api property name to builder if the
     * annotation exists
     */
    @Override
    public <T> void decorate(MappedField.Builder<T> b, BeanInfo<T> bi, PropertyDescriptor pd) {
        JsonIgnore ignore = getAnnotation(JsonIgnore.class, pd);
        if (ignore != null) {
            b.apiName(null);
        } else {
            JsonProperty annotation = getAnnotation(JsonProperty.class, pd);
            if (annotation != null) {
                b.apiName(annotation.value());
            }
        }
    }

}
