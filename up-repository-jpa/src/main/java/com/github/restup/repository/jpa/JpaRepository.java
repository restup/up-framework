package com.github.restup.repository.jpa;

import com.github.restup.annotations.operations.CreateResource;
import com.github.restup.annotations.operations.DeleteResource;
import com.github.restup.annotations.operations.UpdateResource;
import com.github.restup.query.ResourceQueryDefaults;
import com.github.restup.registry.Resource;
import com.github.restup.service.model.request.CreateRequest;
import com.github.restup.service.model.request.DeleteRequest;
import com.github.restup.service.model.request.UpdateRequest;
import com.github.restup.service.model.response.PersistenceResult;
import com.github.restup.util.UpRepositoryUtils;
import java.io.Serializable;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

public class JpaRepository<T, ID extends Serializable> extends ReadOnlyJpaRepository<T, ID> {

    public JpaRepository(EntityManager entityManager) {
        super(entityManager);
    }

    @CreateResource
    @Transactional
    public T create(CreateRequest<T> request) {
        T t = request.getData();
        save(t);
        return t;
    }

    @DeleteResource
    @Transactional
    public T delete(DeleteRequest<T, ID> request) {
        //TODO apply additional query criteria for optimistic updates?
        T t = findOne((Resource<T, ID>) request.getResource(), request.getId());
        if (t != null) {
            EntityManager em = getEntityManager();
            em.remove(t);
        }
        return t;
    }

    @Transactional
    @UpdateResource
    public PersistenceResult<T> update(UpdateRequest<T, ID> request, ResourceQueryDefaults defaults) {
        //TODO apply additional query criteria for optimistic updates?
        T t = findOne((Resource<T, ID>) request.getResource(), request.getId());
        UpRepositoryUtils.prepareUpdate(request, defaults, t);
        save(t);
        return UpRepositoryUtils.getPersitenceResult(defaults, request, t);
    }

    private void save(T resource) {
        EntityManager em = getEntityManager();
        em.persist(resource);
    }
}
