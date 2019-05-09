package com.github.restup.repository.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.github.restup.annotations.operations.CreateResource;
import com.github.restup.annotations.operations.DeleteResource;
import com.github.restup.annotations.operations.UpdateResource;
import com.github.restup.path.ResourcePath;
import com.github.restup.query.ResourceQueryDefaults;
import com.github.restup.registry.Resource;
import com.github.restup.service.model.request.CreateRequest;
import com.github.restup.service.model.request.DeleteRequest;
import com.github.restup.service.model.request.UpdateRequest;
import com.github.restup.service.model.response.PersistenceResult;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class DynamoDBRepository<T, ID extends Serializable> extends
    ReadOnlyDynamoDBRepository<T, ID> {

    public DynamoDBRepository(DynamoDBMapper mapper) {
        super(mapper);
    }

    @CreateResource
    public T create(CreateRequest<T> request) {
        T t = request.getData();
        save(t);
        return t;
    }

    @DeleteResource
    public T delete(DeleteRequest<T, ID> request) {
        //TODO Should not have to read back but easiest for poc
        T t = findOne((Resource<T, ID>) request.getResource(), request.getId());
        if (t != null) {
            DynamoDBMapper ddb = getMapper();
            ddb.delete(t);
        }
        return t;
    }

    @UpdateResource
    public PersistenceResult<T> update(UpdateRequest<T, ID> request,
        ResourceQueryDefaults defaults) {
        //TODO shouldn't have to read back and update, but easiest for poc
        T t = findOne((Resource<T, ID>) request.getResource(), request.getId());
        T update = request.getData();
        applyUpdate(t, update, request.getRequestedPaths());
        if (defaults != null) {
            applyUpdate(t, update, defaults.getRequiredFields());
        }
        save(t);
        return PersistenceResult.of(t);
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
        DynamoDBMapper ddb = getMapper();
        ddb.save(resource);
    }

}
