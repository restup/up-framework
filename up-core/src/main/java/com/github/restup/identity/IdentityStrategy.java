package com.github.restup.identity;

import java.io.Serializable;

/**
 * Strategy for id generation.
 */
public interface IdentityStrategy<ID extends Serializable> {

    /**
     * @return next id
     */
    ID getNextId();

}
