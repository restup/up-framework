package com.github.restup.repository.jpa;

import com.github.restup.registry.Resource;
import com.github.restup.repository.RepositoryFactory;

import javax.persistence.EntityManager;
import java.io.Serializable;

public class JpaRepositoryFactory implements RepositoryFactory {

    protected JpaRepository<?, ?> repository;

    public JpaRepositoryFactory(EntityManager entityManager) {
        this(new JpaRepository<Object, Serializable>(entityManager));
    }

    public JpaRepositoryFactory(JpaRepository<?, ?> repository) {
        this.repository = repository;
    }

    public Object getRepository(Resource<?, ?> resource) {
        return repository;
    }

}
