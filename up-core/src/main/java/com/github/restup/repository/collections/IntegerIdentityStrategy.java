package com.github.restup.repository.collections;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link IdentityStrategy} for an Integer sequence
 */
public class IntegerIdentityStrategy implements IdentityStrategy<Integer> {

    private volatile AtomicInteger id = new AtomicInteger();

    @Override
    public Integer getNextId() {
        return id.incrementAndGet();
    }

}
