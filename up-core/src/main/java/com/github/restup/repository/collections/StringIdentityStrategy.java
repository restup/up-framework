package com.github.restup.repository.collections;

import java.util.UUID;

/**
 * An {@link IdentityStrategy} for String ids returning a new random UUID for each id
 */
public class StringIdentityStrategy implements IdentityStrategy<String> {

    @Override
    public String getNextId() {
        return UUID.randomUUID().toString();
    }

}
