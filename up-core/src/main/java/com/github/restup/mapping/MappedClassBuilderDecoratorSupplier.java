package com.github.restup.mapping;

import java.util.List;

/**
 * Supplies {@link MappedClassBuilderDecorator}s.  Useful for a {@link
 * com.github.restup.repository.ResourceRepository} implementation to provide {@link
 * MappedClassBuilderDecorator}s for required mappings
 */
@FunctionalInterface
public interface MappedClassBuilderDecoratorSupplier {

    List<MappedClassBuilderDecorator> getMappedClassBuilderDecorators();

}
