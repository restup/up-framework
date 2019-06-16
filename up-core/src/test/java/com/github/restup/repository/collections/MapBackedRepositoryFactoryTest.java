package com.github.restup.repository.collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

import com.github.restup.identity.AtomicIntegerIdentityStrategy;
import com.github.restup.identity.AtomicLongIdentityStrategy;
import com.github.restup.identity.UUIDIdentityStrategy;
import java.util.Map;
import org.junit.Test;
import com.github.restup.test.assertions.Assertions;

public class MapBackedRepositoryFactoryTest {

    @Test
    public void testGetStrategy() {
        MapBackedRepositoryFactory factory = new MapBackedRepositoryFactory();
        assertThat(factory.getStrategy(String.class), instanceOf(UUIDIdentityStrategy.class));
        assertThat(factory.getStrategy(Long.class), instanceOf(AtomicLongIdentityStrategy.class));
        assertThat(factory.getStrategy(Integer.class), instanceOf(AtomicIntegerIdentityStrategy.class));
        Assertions.assertThrows(() -> factory.getStrategy(Map.class), IllegalArgumentException.class);
    }
    
}
