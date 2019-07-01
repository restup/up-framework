package com.github.restup.registry.factories.test;

import com.github.restup.mapping.MappedClass;
import com.github.restup.mapping.MappedClassBuilderDecorator;
import com.github.restup.util.ReflectionUtils.BeanInfo;

public class ADisabledClassBuilderDecorator implements MappedClassBuilderDecorator {

    @Override
    public <T> void decorate(MappedClass.Builder<T> builder, BeanInfo<T> bi) {

    }
}
