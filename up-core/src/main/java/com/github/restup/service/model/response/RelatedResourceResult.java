package com.github.restup.service.model.response;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import com.github.restup.registry.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public interface RelatedResourceResult<T, ID extends Serializable> {

    static <T, ID extends Serializable> RelatedResourceResult of(Resource<T, ID> resource,
        ReadResult<List<T>> result) {
        return new BasicRelatedResourceResult(resource, result);
    }

    /**
     * @return result of requested related resource
     */
    ReadResult<List<T>> getResult();

    /**
     * @return resource related to requested resource
     */
    Resource<T, ID> getResource();

    class Builder {

        private List<RelatedResourceResult<?, ?>> results;

        private Builder me() {
            return this;
        }

        public <T, ID extends Serializable> Builder addRelatedResourceResult(
            Resource<T, ID> resource,
            ReadResult<List<T>> relatedResourceResult) {
            if (results == null) {
                results = new ArrayList<>();
            }
            results.add(of(resource, relatedResourceResult));
            return me();
        }

        public Object build(Object result) {
            if (isNotEmpty(results)) {
                if (result instanceof PagedResult) {
                    return PagedResult.of((PagedResult) result, results);
                }
                if (result instanceof PersistenceResult) {
                    return PersistenceResult.of((PersistenceResult) result, results);
                }
                if (result instanceof ReadResult) {
                    return ReadResult.of((ReadResult) result, results);
                }
            }
            return result;
        }
    }
}
