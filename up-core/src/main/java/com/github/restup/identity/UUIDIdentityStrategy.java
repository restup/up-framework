package com.github.restup.identity;

import java.util.UUID;

/**
 * An {@link IdentityStrategy} for String ids returning a new random UUID for each id
 */
public class UUIDIdentityStrategy implements IdentityStrategy<String> {

    @Override
    public String getNextId() {
        return UUID.randomUUID().toString();
    }

}
