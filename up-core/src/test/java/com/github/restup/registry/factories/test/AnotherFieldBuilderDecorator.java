package com.github.restup.registry.factories.test;

import com.github.restup.mapping.fields.MappedField;
import com.github.restup.mapping.fields.MappedFieldBuilderDecorator;
import com.github.restup.util.ReflectionUtils.BeanInfo;
import com.github.restup.util.ReflectionUtils.PropertyDescriptor;

public class AnotherFieldBuilderDecorator implements MappedFieldBuilderDecorator {

    @Override
    public <T> void decorate(MappedField.Builder<T> b, BeanInfo<T> bi, PropertyDescriptor pd) {

    }
}
