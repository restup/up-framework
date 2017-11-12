package com.github.restup.mapping.fields.visitors;

import com.github.restup.mapping.fields.MappedField;
import com.github.restup.mapping.fields.MappedFieldBuilderVisitor;
import com.github.restup.util.ReflectionUtils.BeanInfo;
import com.github.restup.util.ReflectionUtils.PropertyDescriptor;
import java.util.Objects;

/**
 * {@link MappedFieldBuilderVisitor} that will mark the {@link MappedField} as an identity field
 * based upon naming convention, matching a specified field (id by default)
 */
public class IdentityByConventionMappedFieldBuilderVisitor implements MappedFieldBuilderVisitor {

    private final String name;

    public IdentityByConventionMappedFieldBuilderVisitor(String name) {
        this.name = name;
    }

    public IdentityByConventionMappedFieldBuilderVisitor() {
        this("id");
    }

    public <T> void visit(MappedField.Builder<T> b, BeanInfo<T> bi, PropertyDescriptor pd) {
        if (Objects.equals(name, b.getBeanName())) {
            b.setIdField(true);
        }
    }

}
