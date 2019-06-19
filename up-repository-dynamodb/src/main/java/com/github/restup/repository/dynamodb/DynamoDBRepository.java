package com.github.restup.repository.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.github.restup.annotations.operations.CreateResource;
import com.github.restup.annotations.operations.DeleteResource;
import com.github.restup.annotations.operations.UpdateResource;
import com.github.restup.identity.IdentityStrategy;
import com.github.restup.identity.UUIDIdentityStrategy;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.query.ResourceQueryDefaults;
import com.github.restup.registry.Resource;
import com.github.restup.service.model.request.CreateRequest;
import com.github.restup.service.model.request.DeleteRequest;
import com.github.restup.service.model.request.UpdateRequest;
import com.github.restup.service.model.response.PersistenceResult;
import com.github.restup.util.ReflectionUtils;
import com.github.restup.util.UpRepositoryUtils;
import java.io.Serializable;

public class DynamoDBRepository<T, ID extends Serializable> extends
    ReadOnlyDynamoDBRepository<T, ID> {

    private final IdentityStrategy<ID> identityStrategy;

    public DynamoDBRepository(DynamoDBMapper mapper, IdentityStrategy<ID> identityStrategy) {
        super(mapper);
        this.identityStrategy = identityStrategy;
    }

    public DynamoDBRepository(DynamoDBMapper mapper) {
        this(mapper, (IdentityStrategy) new UUIDIdentityStrategy());
    }

    @CreateResource
    public T create(CreateRequest<T> request) {
        T t = request.getData();
        MappedField identityField = request.getResource().getIdentityField();
        if (null == identityField.readValue(t)) {
            identityField.writeValue(t, identityStrategy.getNextId());
        }
        save(t);
        return t;
    }

    @DeleteResource
    public T delete(DeleteRequest<T, ID> request) {
        Resource resource = request.getResource();
        T t = (T) ReflectionUtils.newInstance(resource.getClassType());
        resource.getIdentityField().writeValue(t, request.getId());
        DynamoDBMapper ddb = getMapper();
        ddb.delete(t);
        return t;
    }

    @UpdateResource
    public PersistenceResult<T> update(UpdateRequest<T, ID> request,
        ResourceQueryDefaults defaults) {
        T t = null;
        if (UpRepositoryUtils.isContentRequired(defaults, request)) {
            t = findOne((Resource<T, ID>) request.getResource(), request.getId());
            UpRepositoryUtils.prepareUpdate(request, defaults, t);
        } else {
            t = request.getData();
        }
        save(t);
        return UpRepositoryUtils.getPersitenceResult(defaults, request, t);
    }

    private void save(T resource) {
        DynamoDBMapper ddb = getMapper();
        ddb.save(resource);
    }

}
