package com.github.restup.repository;

import com.github.restup.registry.Resource;

/**
 * Optional factory to provide repository implementations.
 *
 * @author abuttaro
 */
public interface RepositoryFactory {

    Object getRepository(Resource<?, ?> resource);

}
