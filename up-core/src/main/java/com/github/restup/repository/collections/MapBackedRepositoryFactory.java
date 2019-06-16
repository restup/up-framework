package com.github.restup.repository.collections;

import com.github.restup.identity.AtomicIntegerIdentityStrategy;
import com.github.restup.identity.AtomicLongIdentityStrategy;
import com.github.restup.identity.IdentityStrategy;
import com.github.restup.identity.UUIDIdentityStrategy;
import com.github.restup.registry.Resource;
import com.github.restup.repository.RepositoryFactory;
import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

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
        map.put(String.class, new Supplier<UUIDIdentityStrategy>() {
            @Override
            public UUIDIdentityStrategy get() {
                return new UUIDIdentityStrategy();
            }
        });
        map.put(Long.class, new Supplier<AtomicLongIdentityStrategy>() {
            @Override
            public AtomicLongIdentityStrategy get() {
                return new AtomicLongIdentityStrategy();
            }
        });
        map.put(Integer.class, new Supplier<AtomicIntegerIdentityStrategy>() {
            @Override
            public AtomicIntegerIdentityStrategy get() {
                return new AtomicIntegerIdentityStrategy();
            }
        });
        return map;
    }

    @Override
    public Object getRepository(Resource resource) {
        IdentityStrategy strategy = getStrategy(resource.getIdentityField().getType());
        return new MapBackedRepository(strategy);
    }

    IdentityStrategy getStrategy(Type type) {
        Supplier<? extends IdentityStrategy> strategy = map.get(type);
        if ( strategy == null ) {
            throw new IllegalArgumentException("Unable to provide a strategy for " + type);
        }
        return strategy.get();
    }
}
