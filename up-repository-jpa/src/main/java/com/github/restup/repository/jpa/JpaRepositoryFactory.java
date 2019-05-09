package com.github.restup.repository.jpa;

import com.github.restup.registry.Resource;
import com.github.restup.repository.RepositoryFactory;
import javax.persistence.EntityManager;

public class JpaRepositoryFactory implements RepositoryFactory {

    private final JpaRepository<?, ?> repository;

    public JpaRepositoryFactory(EntityManager entityManager) {
        this(new JpaRepository<>(entityManager));
    }

    public JpaRepositoryFactory(JpaRepository<?, ?> repository) {
        this.repository = repository;
    }

    @Override
    public Object getRepository(Resource<?, ?> resource) {
        return repository;
    }

}
