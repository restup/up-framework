package com.github.restup.mapping;

import java.util.List;

public interface PolymorphicMappedClass<T> extends MappedClass<T> {

    List<Class<?>> getSubTypes();

}
