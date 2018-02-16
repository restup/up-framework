package com.github.restup.query;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import com.github.restup.bind.param.NoOpParameterProvider;
import com.github.restup.errors.ErrorCode;
import com.github.restup.errors.Errors;
import com.github.restup.errors.RequestError;
import com.github.restup.path.ResourcePath;
import com.github.restup.query.criteria.ResourcePathFilter;
import com.github.restup.query.criteria.ResourcePathFilter.Operator;
import com.github.restup.query.criteria.ResourceQueryCriteria;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.service.ResourceService;
import com.github.restup.service.model.request.ListRequest;
import com.github.restup.service.model.request.RequestObjectFactory;
import com.github.restup.service.model.response.ReadResult;
import com.github.restup.util.Assert;

/**
 * Provides a ResourceQuery Builder for building and executing resource queries programmatically.
 */
public class ResourceQuery {

    //TODO doc

    private ResourceQuery() {

    }

    /**
     * Convenience method for finding a resource by id
     *
     * @param registry containing meta data about the resource
     * @param resourceName resource to find
     * @param id the id of the resource to find
     * @param <T> type of class to be found
     * @return resource found
     */
    @SuppressWarnings("unchecked")
    public static <T> T find(ResourceRegistry registry, String resourceName, Object id) {
        return (T) query(registry, resourceName).filterById(id).get();
    }

    public static <T> T find(Resource<T, ?> resource, Object id) {
        return new Builder<T>(resource.getRegistry(), resource.getName()).filterById(id).get();
    }

    /**
     * Create a new query builder
     *
     * @param registry containing meta data about the resource
     * @param resourceName resource to find
     * @param <T> type of class to be found
     * @return this builder
     */
    public static <T> Builder<T> query(ResourceRegistry registry, String resourceName) {
        return new Builder<T>(registry, resourceName);
    }

    public static <T> Builder<T> query(Resource<T, ?> resource) {
        return query(resource.getRegistry(), resource.getName());
    }

    public static <T> Builder<T> query(ResourceQueryStatement query, Errors errors) {
        return new Builder<T>(query, errors);
    }

    public static <T> Builder<T> query(ResourceQueryStatement query) {
        return new Builder<T>(query);
    }

    public final static class Builder<T> {

        private final Resource<T, ?> resource;
        private final RequestObjectFactory factory;
        private ResourceQueryStatement.Builder query;

        @SuppressWarnings("unchecked")
        Builder(ResourceRegistry registry, String resourceName) {
            super();
            Assert.notNull(registry, "registry is required");
            Assert.notNull(resourceName, "resourceName is required");
            this.resource = (Resource<T, ?>) registry.getResource(resourceName);
            this.factory = registry.getSettings().getRequestObjectFactory();
            Assert.notNull(resource, "resource is required");
            query = ResourceQueryStatement.builder(resource);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        Builder(ResourceQueryStatement query, Errors errors) {
            super();
            Assert.notNull(query, "query is required");
            this.resource = (Resource) query.getResource();
            this.factory = query.getResource().getRegistry().getSettings().getRequestObjectFactory();
            this.query = ResourceQueryStatement.builder(query, errors);
        }

        Builder(ResourceQueryStatement query) {
            this(query, null);
        }

        private Builder<T> me() {
            return this;
        }

        public Builder<T> filter(ResourceQueryCriteria filter) {
            query.addCriteria(filter);
            return me();
        }

        public Builder<T> filter(String path, Object value) {
            return filter(path, Operator.eq, value);
        }

        public Builder<T> filter(ResourcePath path, Object value) {
            return filter(path, Operator.eq, value);
        }

        public Builder<T> filter(String path, Operator operator, Object value) {
            ResourcePath rp = ResourcePath.path(resource, path);
            return filter(rp, operator, value);
        }

        public Builder<T> filter(ResourcePath path, Operator operator, Object value) {
            return filter(new ResourcePathFilter<Object>(path, operator, value));
        }

        public Builder<?> filter(List<ResourcePath> paths, Object value) {
            return filter(paths, Operator.eq, value);
        }

        public Builder<?> filter(List<ResourcePath> paths, Operator operator, Object value) {
            query.addCriteria(paths, operator, value);
            return me();
        }

        public Builder<T> filterById(Object id) {
            return filter(getIdentityFieldPath(), id);
        }

        private ResourcePath getIdentityFieldPath() {
            return ResourcePath.idPath(resource);
        }

        private ResourceService<T, ?> getService() {
            return resource.getService();
        }

        public List<T> list() {
            ReadResult<List<T>> result = result();
            return result == null ? null : result.getData();
        }

        public ReadResult<List<T>> result() {
            ResourceService<T, ?> service = getService();
            List<ResourceQueryStatement> queries = Arrays.asList(query.build());
            ListRequest<T> request = factory.getListRequest(resource, queries, NoOpParameterProvider.getInstance());
            return service.list(request);
        }

        public T get() {
            List<T> result = list();
            int n = CollectionUtils.size(result);
            if (n < 1) {
                return null;
            } else if (n > 1) {
                RequestError.builder()
                        .code(ErrorCode.UNEXPECTED_FIND_RESULTS)
                        .meta("size", n)
                        .throwError();
            }
            return result.get(0);
        }
    }
}
