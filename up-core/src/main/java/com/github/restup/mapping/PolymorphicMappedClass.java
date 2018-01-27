package com.github.restup.mapping;

import java.util.List;

public interface PolymorphicMappedClass<T> {

    List<Class<?>> getSubTypes();

}
