package com.github.restup.repository.collections;

import com.github.restup.registry.Resource;
import com.github.restup.repository.RepositoryFactory;

/**
 * {@link RepositoryFactory} for creating {@link MapBackedRepository} instances
 */
public class MapBackedRepositoryFactory implements RepositoryFactory {

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Object getRepository(Resource resource) {
        IdentityStrategy strategy = getStrategy(resource.getIdentityField().getType());
        return new MapBackedRepository(strategy);
    }

    @SuppressWarnings("rawtypes")
    private IdentityStrategy getStrategy(Class type) {
        if (type == String.class) {
            return new StringIdentityStrategy();
        }
        if (type == Long.class) {
            return new LongIdentityStrategy();
        }
        if (type == Integer.class) {
            return new IntegerIdentityStrategy();
        }
        throw new IllegalArgumentException("Unable to provide a strategy for " + type);
    }
}
