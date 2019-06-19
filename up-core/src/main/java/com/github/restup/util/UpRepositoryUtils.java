package com.github.restup.util;

import com.github.restup.annotations.model.UpdateStrategy;
import com.github.restup.path.ResourcePath;
import com.github.restup.query.ResourceQueryDefaults;
import com.github.restup.service.model.request.UpdateRequest;
import com.github.restup.service.model.response.PersistenceResult;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class UpRepositoryUtils {


    private UpRepositoryUtils() {

    }

    public static <T, ID extends Serializable> void prepareUpdate(UpdateRequest<T, ID> request,
        ResourceQueryDefaults defaults, T t) {

        T update = request.getData();
        applyUpdate(t, update, request.getRequestedPaths());
        if (defaults != null) {
            applyUpdate(t, update, defaults.getRequiredFields());
        }
    }

    private static <T, ID extends Serializable> void applyUpdate(T t, T update,
        List<ResourcePath> requestedPaths) {
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

    public static <ID extends Serializable, T> boolean isContentRequired(
        ResourceQueryDefaults defaults,
        UpdateRequest<T, ID> request) {
        return defaults.hasRequiredFields()
            || Objects.equals(UpdateStrategy.UPDATED, request.getUpdateStrategy());
    }

    public static <T, ID extends Serializable> PersistenceResult<T> getPersitenceResult(
        ResourceQueryDefaults defaults,
        UpdateRequest<T, ID> request, T t) {
        UpdateStrategy updateStrategy =
            isContentRequired(defaults, request) ? UpdateStrategy.UPDATED
                : request.getUpdateStrategy();
        return PersistenceResult.of(t, updateStrategy);
    }
}
