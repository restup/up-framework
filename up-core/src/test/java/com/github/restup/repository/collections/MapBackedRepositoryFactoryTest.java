package com.github.restup.repository.collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import java.util.Map;
import org.junit.Test;
import com.github.restup.test.assertions.Assertions;

public class MapBackedRepositoryFactoryTest {

    @Test
    public void testGetStrategy() {
        MapBackedRepositoryFactory factory = new MapBackedRepositoryFactory();
        assertThat(factory.getStrategy(String.class), instanceOf(StringIdentityStrategy.class));
        assertThat(factory.getStrategy(Long.class), instanceOf(LongIdentityStrategy.class));
        assertThat(factory.getStrategy(Integer.class), instanceOf(IntegerIdentityStrategy.class));
        Assertions.assertThrows(() -> factory.getStrategy(Map.class), IllegalArgumentException.class);
    }
    
}
