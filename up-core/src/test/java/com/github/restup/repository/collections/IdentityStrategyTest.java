package com.github.restup.repository.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

public class IdentityStrategyTest {

    @Test
    public void testStringIdentityStrategy() {
        assertUniqueIds(new StringIdentityStrategy());
    }
    @Test
    public void testIntegerIdentityStrategy() {
        assertUniqueIds(new IntegerIdentityStrategy());
    }
    @Test
    public void testLongIdentityStrategy() {
        assertUniqueIds(new LongIdentityStrategy());
    }

    private void assertUniqueIds(IdentityStrategy<?> strategy) {
        Set<Object> set = new HashSet<>();
        int n = 10;
        for ( int i=0; i < n; i++) {
            Object id = strategy.getNextId();
            assertFalse(set.contains(id));
            set.add(id);
        }
        assertEquals(n, set.size());
    }

}
