package com.github.restup.query;

import static com.github.restup.util.UpUtils.unmodifiableList;

import com.github.restup.errors.Errors;
import com.github.restup.path.ResourcePath;
import com.github.restup.path.ResourcePath.Builder.Mode;
import com.github.restup.query.criteria.ResourcePathFilter;
import com.github.restup.query.criteria.ResourcePathFilter.Operator;
import com.github.restup.query.criteria.ResourceQueryCriteria;
import com.github.restup.registry.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a resource query including all fields, criteria, paging behavior and sort criteria.
 */
public class ResourceQueryStatement extends AbstractResourceQueryStatement {

    private final Type type;
    // fields selected
    private final List<ResourcePath> requestedPaths;
    private final List<ResourcePath> requiredRelationshipPaths;
    private final List<ResourcePath> requestedPathsExcluded;
    private final List<ResourcePath> requestedPathsAdded;

    ResourceQueryStatement(Type type, Resource<?, ?> resource, List<ResourcePath> requestedPaths, List<ResourcePath> requiredRelationshipPaths,
            List<ResourcePath> requestedPathsExcluded, List<ResourcePath> requestedPathsAdded,
            List<ResourceQueryCriteria> requestedFilters, List<ResourceSort> requestedSort,
            Pagination pagination) {
        super(resource, requestedFilters, requestedSort, pagination);
        this.type = type;
        this.requestedPaths = unmodifiableList(requestedPaths);
        this.requestedPathsExcluded = unmodifiableList(requestedPathsExcluded);
        this.requestedPathsAdded = unmodifiableList(requestedPathsAdded);
        this.requiredRelationshipPaths = unmodifiableList(requiredRelationshipPaths);
    }

    public static ResourceQueryStatement getQuery(Resource<?, ?> requestedResource, List<ResourceQueryStatement> queries) {
        if (queries != null) {
            for (ResourceQueryStatement q : queries) {
                if (Objects.equals(q.getResource(), requestedResource)) {
                    return q;
                }
            }
        }
        return null;
    }

    public static Type getType(ResourceQueryStatement query) {
        return query == null ? Type.Default : query.getType();
    }

    public static Builder builder(Resource<?, ?> resource) {
        return new Builder(resource);
    }

    public static Builder builder(Resource<?, ?> resource, Errors errors) {
        return new Builder(resource, errors);
    }

    public static Builder builder(ResourceQueryStatement query, Errors errors) {
        return new Builder(query, errors);
    }

    public boolean hasRequestedPaths(String path) {
        return requestedPaths.contains(ResourcePath.path(resource, path));
    }

    public boolean hasRequestedPathsExcluded(String path) {
        return requestedPathsExcluded.contains(ResourcePath.path(resource, path));
    }

    public boolean hasRequestedPathsAdded(String path) {
        return requestedPathsAdded.contains(ResourcePath.path(resource, path));
    }

    /**
     * @return Basic requested paths... foo, bar
     */
    public List<ResourcePath> getRequestedPaths() {
        return requestedPaths;
    }

    /**
     * @return Paths required based upon a requested relationship
     */
    public List<ResourcePath> getRequiredRelationshipPaths() {
        return requiredRelationshipPaths;
    }

    /**
     * @return Paths requested to be excluded from response... fields=-bar
     */
    public List<ResourcePath> getRequestedPathsExcluded() {
        return requestedPathsExcluded;
    }

    /**
     * @return Paths requested to be included in response... fields=+foo
     */
    public List<ResourcePath> getRequestedPathsAdded() {
        return requestedPathsAdded;
    }

    public Type getType() {
        return type;
    }

    @Override
    public Resource<?, ?> getResource() {
        return resource;
    }

    public enum Type {
        /**
         * No fields requested
         */
        Default,
        /**
         * Specific fields requested
         */
        Sparse,
        /**
         * All non transient fields
         */
        All,
        /**
         * All transient and non transient fields
         */
        Every

    }

    public static class Builder {

        private final Resource<?, ?> resource;
        private Type type;
        private Errors errors;
        private List<ResourcePath> requestedPaths;
        private List<ResourcePath> requiredJoinPaths;
        private List<ResourcePath> requestedPathsExcluded;
        private List<ResourcePath> requestedPathsAdded;
        private Integer pageLimit;
        private Integer pageOffset;
        private Boolean pagingDisabled;
        private Boolean withTotalsDisabled;
        private Mode mode;
        private List<ResourceQueryCriteria> requestedCriteria;
        private List<ResourceSort> requestedSort;

        public Builder(Resource<?, ?> resource, Errors errors) {
            this.resource = resource;
            this.errors = errors;
            mode = Mode.API;
        }

        public Builder(ResourceQueryStatement query, Errors errors) {
            resource = query.getResource();
            this.errors = errors;
            mode = Mode.API;

            Pagination page = query.getPagination();
            setType(query.getType())
                    .addRequestedPaths(query.getRequestedPaths())
                    .addRequestedPathsAdded(query.getRequestedPathsAdded())
                    .addRequestedPathsExcluded(query.getRequestedPathsExcluded())
                    .setPageLimit(page.getLimit())
                    .setPageOffset(page.getOffset())
                    .setPagingDisabled(page.isPagingDisabled())
                    .setWithTotalsDisabled(page.isWithTotalsDisabled())
                    .addCriteria(query.getRequestedCriteria())
                    .addSort(query.getRequestedSort());
        }

        public Builder(Resource<?, ?> resource) {
            this(resource, null);
        }

        private Builder me() {
            return this;
        }

        public Builder setType(Type type) {
            if (this.type == null || this.type.compareTo(type) < 0) {
                this.type = type;
            }
            return me();
        }

        public Builder setMode(Mode mode) {
            this.mode = mode;
            return me();
        }

        private ResourcePath path(String path) {
            return path(resource, path);
        }

        private ResourcePath path(Resource<?, ?> resource, String path) {
            return ResourcePath.builder(resource)
                    .setMode(mode)
                    .setErrors(errors)
                    .path(path)
                    .build();
        }

        public Builder addRequestedPaths(Collection<ResourcePath> paths) {
            for (ResourcePath path : paths) {
                addRequestedPaths(path);
            }
            return me();
        }

        public Builder addRequestedPathsAdded(Collection<ResourcePath> paths) {
            for (ResourcePath path : paths) {
                addRequestedPathsAdded(path);
            }
            return me();
        }

        public Builder addRequestedPathsExcluded(Collection<ResourcePath> paths) {
            for (ResourcePath path : paths) {
                addRequestedPathsExcluded(path);
            }
            return me();
        }

        public Builder addRequestedPaths(String... paths) {
            for (String path : paths) {
                addRequestedPaths(path(path));
            }
            return me();
        }

        public Builder addRequestedPaths(ResourcePath path) {
            setType(Type.Sparse);
            requestedPaths = add(requestedPaths, path);
            return me();
        }

        public Builder addIncludeJoinPaths(Resource<?, ?> requestedResource, String path) {
            ResourcePath requestResourceAccessor = path(requestedResource, path);
            ResourcePath criteriaPath;
            //TODO jo
            if (requestResourceAccessor.isValid()) {
                // if path is valid on requestedResource, then the join is on the id of
                // the included resource
                criteriaPath = ResourcePath.idPath(resource);
            } else {
                // otherwise it'd better be a valid path on the included resource
                criteriaPath = path(resource, path);
                if (criteriaPath.isValid()) {
                    requestResourceAccessor = ResourcePath.idPath(requestedResource);
                }
            }
            return addIncludeJoinPaths(path(path));
        }

        public Builder addIncludeJoinPaths(ResourcePath path) {
            requiredJoinPaths = add(requiredJoinPaths, path);
            return me();
        }

        public Builder addRequestedPathsExcluded(String path) {
            return addRequestedPathsExcluded(path(path));
        }

        public Builder addRequestedPathsExcluded(ResourcePath path) {
            requestedPathsExcluded = add(requestedPathsExcluded, path);
            return me();
        }

        public Builder addRequestedPathsAdded(String... paths) {
            for (String path : paths) {
                addRequestedPathsAdded(path(path));
            }
            return me();
        }

        public Builder addRequestedPathsAdded(ResourcePath path) {
            requestedPathsAdded = add(requestedPathsAdded, path);
            return me();
        }

        public Builder setPageLimit(Integer pageLimit) {
            this.pageLimit = pageLimit;
            return me();
        }

        public Builder setPageOffset(Integer pageOffset) {
            this.pageOffset = pageOffset;
            return me();
        }

        public Builder setPagingEnabled(boolean pagingEnabled) {
            return setPagingDisabled(!pagingEnabled);
        }

        public Builder setPagingDisabled(boolean pagingDisabled) {
            this.pagingDisabled = pagingDisabled;
            return me();
        }

        public Builder setWithTotalsEnabled(boolean withTotalsEnabled) {
            return setWithTotalsDisabled(!withTotalsEnabled);
        }

        public Builder setWithTotalsDisabled(boolean withTotalsDisabled) {
            this.withTotalsDisabled = withTotalsDisabled;
            return me();
        }

        public Builder addCriteria(Collection<ResourceQueryCriteria> filters) {
            if (filters != null) {
                for (ResourceQueryCriteria filter : filters) {
                    addCriteria(filter);
                }
            }
            return me();
        }

        public Builder addCriteria(ResourceQueryCriteria filter) {
            if (requestedCriteria == null) {
                requestedCriteria = new ArrayList<>(5);
            }
            requestedCriteria.add(filter);
            return me();
        }

        public Builder addCriteria(ResourcePath path, ResourcePathFilter.Operator operator, Object value) {
            return addCriteria(new ResourcePathFilter<>(path, operator, value));
        }

        public Builder addCriteria(String field, ResourcePathFilter.Operator operator, Object value) {
            return addCriteria(new ResourcePathFilter<>(resource, field, operator, value));
        }

        public Builder addCriteria(String field, Object value) {
            return addCriteria(new ResourcePathFilter<>(resource, field, value));
        }

        public Builder addCriteria(List<ResourcePath> paths, Object value) {
            return addCriteria(paths, Operator.eq, value);
        }

        public Builder addCriteria(List<ResourcePath> paths, Operator operator, Object value) {
            int size = paths.size();
            if (size == 1) {
                return addCriteria(paths.get(0), operator, value);
            } else if (size > 1) {
                List<ResourceQueryCriteria> criteria = new ArrayList<>();
                for (ResourcePath path : paths) {
                    criteria.add(new ResourcePathFilter<>(path, operator, value));
                }
                return addCriteria(ResourceQueryCriteria.or(criteria));
            }
            return me();
        }

        public Builder addSort(Collection<ResourceSort> sorts) {
            if (sorts != null) {
                for (ResourceSort sort : sorts) {
                    addSort(sort);
                }
            }
            return me();
        }

        public Builder addSort(ResourceSort resourceSort) {
            if (requestedSort == null) {
                requestedSort = new ArrayList<>(3);
            }
            requestedSort.add(resourceSort);
            return me();
        }

        private List<ResourcePath> add(List<ResourcePath> set, ResourcePath path) {
            List<ResourcePath> result = set;
            if (result == null) {
                result = new ArrayList<>();
            }
            if (!result.contains(path)) {
                result.add(path);
            }
            return result;
        }

        public ResourceQueryStatement build() {
            Pagination pagination = resource.getDefaultPagination();
            if (pageLimit != null || pageOffset != null || withTotalsDisabled != null || pagingDisabled != null) {
                Integer limit = pageLimit != null ? pageLimit : pagination.getLimit();
                Integer offset = pageOffset != null ? pageOffset : pagination.getOffset();
                boolean disablePaging = pagingDisabled != null ? pagingDisabled : pagination.isPagingDisabled();
                boolean disableTotals = withTotalsDisabled != null ? withTotalsDisabled : pagination.isWithTotalsDisabled();

                pagination = disablePaging ? Pagination.disabled() : Pagination.of(limit, offset, disableTotals);
            }
            Type t = type;
            if (t == null) {
                t = Type.Default;
            }
            return new ResourceQueryStatement(t, resource, requestedPaths, null, requestedPathsExcluded, requestedPathsAdded, requestedCriteria, requestedSort, pagination);
        }
    }

}
