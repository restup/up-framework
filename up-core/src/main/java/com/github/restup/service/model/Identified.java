package com.github.restup.service.model;

import java.io.Serializable;

/**
 * Promotes consistency for objects with an id
 *
 * @param <ID>
 */
public interface Identified<ID extends Serializable> {

    ID getId();

}
