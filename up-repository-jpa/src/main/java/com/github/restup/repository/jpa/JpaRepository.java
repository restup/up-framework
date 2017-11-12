package com.github.restup.repository.jpa;

import com.github.restup.annotations.operations.CreateResource;
import com.github.restup.annotations.operations.DeleteResource;
import com.github.restup.annotations.operations.UpdateResource;
import com.github.restup.path.ResourcePath;
import com.github.restup.query.ResourceQueryDefaults;
import com.github.restup.registry.Resource;
import com.github.restup.service.model.request.CreateRequest;
import com.github.restup.service.model.request.DeleteRequest;
import com.github.restup.service.model.request.UpdateRequest;
import com.github.restup.service.model.response.BasicPersistenceResult;
import com.github.restup.service.model.response.PersistenceResult;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;

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
    @SuppressWarnings("unchecked")
    public T delete(DeleteRequest<T, ID> request) {
        //TODO apply additional query criteria for optimistic updates?
        T t = findOne((Resource<T, ID>) request.getResource(), request.getId());
        if (t != null) {
            EntityManager em = getEntityManager();
            em.remove(t);
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @UpdateResource
    public PersistenceResult<T> update(UpdateRequest<T, ID> request, ResourceQueryDefaults defaults) {
        //TODO apply additional query criteria for optimistic updates?
        T t = findOne((Resource<T, ID>) request.getResource(), request.getId());
        T update = request.getData();
        applyUpdate(t, update, request.getRequestedPaths());
        if (defaults != null) {
            applyUpdate(t, update, defaults.getRequiredFields());
        }
        save(t);
        return new BasicPersistenceResult<T>(t);
    }


    private void applyUpdate(T t, T update, List<ResourcePath> requestedPaths) {
        if (requestedPaths != null) {
            for (ResourcePath p : requestedPaths) {
                Object currentValue = p.getValue(t);
                Object newValue = p.getValue(update);
                if (!Objects.equals(currentValue, newValue)) {
                    // TODO collect diff for result
                    p.setValue(t, p.getValue(update));
                }
            }
        }
    }

    private void save(T resource) {
        EntityManager em = getEntityManager();
        em.persist(resource);
    }
}
