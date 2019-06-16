package com.github.restup.identity;

import java.util.concurrent.atomic.AtomicLong;

/**
 * {@link IdentityStrategy} for a Long sequence
 */
public class AtomicLongIdentityStrategy implements IdentityStrategy<Long> {

    private volatile AtomicLong id = new AtomicLong();

    @Override
    public Long getNextId() {
        return id.incrementAndGet();
    }

}
