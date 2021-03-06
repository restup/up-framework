package com.github.restup.repository.collections;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import com.github.restup.registry.Resource;
import com.github.restup.repository.RepositoryFactory;
import com.google.common.collect.ImmutableMap;

/**
 * {@link RepositoryFactory} for creating {@link MapBackedRepository} instances
 */
public class MapBackedRepositoryFactory implements RepositoryFactory {
    
    private final Map<Type, Supplier<? extends IdentityStrategy<?>>> map; 

    public MapBackedRepositoryFactory(Map<Type, Supplier<? extends IdentityStrategy<?>>> map) {
        this.map = ImmutableMap.copyOf(map);
    }

    public MapBackedRepositoryFactory() {
        this(defaultIdentityStrategies());
    }

    private static Map<Type, Supplier<? extends IdentityStrategy<?>>> defaultIdentityStrategies() {
        Map<Type, Supplier<? extends IdentityStrategy<?>>> map = new HashMap<>();
        map.put(String.class, new Supplier<StringIdentityStrategy>() {
            @Override
            public StringIdentityStrategy get() {
                return new StringIdentityStrategy();
            }
        });
        map.put(Long.class, new Supplier<LongIdentityStrategy>() {
            @Override
            public LongIdentityStrategy get() {
                return new LongIdentityStrategy();
            }
        });
        map.put(Integer.class, new Supplier<IntegerIdentityStrategy>() {
            @Override
            public IntegerIdentityStrategy get() {
                return new IntegerIdentityStrategy();
            }
        });
        return map;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Object getRepository(Resource resource) {
        IdentityStrategy strategy = getStrategy(resource.getIdentityField().getType());
        return new MapBackedRepository(strategy);
    }

    @SuppressWarnings("rawtypes")
    IdentityStrategy getStrategy(Type type) {
        Supplier<? extends IdentityStrategy> strategy = map.get(type);
        if ( strategy == null ) {
            throw new IllegalArgumentException("Unable to provide a strategy for " + type);
        }
        return strategy.get();
    }
}
