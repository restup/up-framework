package com.github.restup.repository.collections;

import java.util.concurrent.atomic.AtomicLong;

/**
 * {@link IdentityStrategy} for a Long sequence
 */
public class LongIdentityStrategy implements IdentityStrategy<Long> {

    private volatile AtomicLong id = new AtomicLong();

    @Override
    public Long getNextId() {
        return id.incrementAndGet();
    }

}
