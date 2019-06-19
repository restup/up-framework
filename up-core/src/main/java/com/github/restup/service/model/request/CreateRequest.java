package com.github.restup.service.model.request;

import com.github.restup.annotations.model.CreateStrategy;

/**
 * Request interface for create operations
 */
public interface CreateRequest<T> extends PersistenceRequest<T> {

    CreateStrategy getCreateStrategy();

}
