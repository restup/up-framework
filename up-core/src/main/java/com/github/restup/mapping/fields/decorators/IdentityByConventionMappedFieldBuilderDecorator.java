package com.github.restup.mapping.fields.decorators;

import com.github.restup.mapping.fields.MappedField;
import com.github.restup.mapping.fields.MappedFieldBuilderDecorator;
import com.github.restup.util.ReflectionUtils.BeanInfo;
import com.github.restup.util.ReflectionUtils.PropertyDescriptor;
import java.util.Objects;

/**
 * {@link MappedFieldBuilderDecorator} that will mark the {@link com.github.restup.mapping.fields.MappedField}
 * as an identity field based upon naming convention, matching a specified field (id by default)
 */
public class IdentityByConventionMappedFieldBuilderDecorator implements
    MappedFieldBuilderDecorator {

    private final String name;

    public IdentityByConventionMappedFieldBuilderDecorator(String name) {
        this.name = name;
    }

    public IdentityByConventionMappedFieldBuilderDecorator() {
        this("id");
    }

    @Override
    public <T> void decorate(MappedField.Builder<T> b, BeanInfo<T> bi, PropertyDescriptor pd) {
        if (Objects.equals(name, b.getBeanName())) {
            b.idField(true);
        }
    }

}
