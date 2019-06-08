package com.github.restup.mapping.fields;

import java.util.List;

@FunctionalInterface
public interface MappedFieldBuilderDecoratorSupplier {

    List<MappedFieldBuilderDecorator> getMappedFieldBuilderDecorators();

}
