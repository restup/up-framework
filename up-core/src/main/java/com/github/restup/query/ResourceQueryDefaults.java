package com.github.restup.query;

import com.github.restup.path.ResourcePath;
import com.github.restup.query.criteria.ListCriteria;
import com.github.restup.query.criteria.ResourcePathFilter;
import com.github.restup.query.criteria.ResourcePathFilter.Operator;
import com.github.restup.query.criteria.ResourceQueryCriteria;
import com.github.restup.registry.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Provides defaults for queries.  For example, may define <p> <li>Fields required for some internal logic, but not requested in sparse field request <li>Filters applied by default for some access control or hiding inactive data by default <li>Default sort criteria. <p> This is a mutable object, which may be the argument of multiple filter methods setting values as needed.
 *
 * @author abuttaro
 */
public class ResourceQueryDefaults {

    private final Resource<?, ?> resource;
    private final ResourceQueryStatement requestedQuery;

    private List<ResourcePath> requiredFields;
    // query criteria
    private List<ResourceQueryCriteria> criteria;
    // sorting
    private List<ResourceSort> sort;

    public ResourceQueryDefaults(Resource<?, ?> resource, ResourceQueryStatement requestedQuery) {
        this.resource = resource;
        this.requestedQuery = requestedQuery;
    }

    /**
     * null safe convenience method
     *
     * @return true if default criteria is not null, false otherwise
     */
    public static boolean hasCriteria(ResourceQueryDefaults defaults) {
        return defaults != null && defaults.getCriteria() != null;
    }

    private boolean isPresentInRequest(ResourcePathFilter<?> criteria) {
        if (criteria != null && requestedQuery != null) {
            for (ResourceQueryCriteria c : requestedQuery.getRequestedCriteria()) {
                if (filterPathEquals(c, criteria)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean filterPathEquals(ResourceQueryCriteria a, ResourcePathFilter<?> b) {
        if (a instanceof ResourcePathFilter) {
            ResourcePathFilter<?> aa = (ResourcePathFilter<?>) a;
            if (Objects.equals(b.getPath(), aa.getPath())) {
                return true;
            }
        } else if (a instanceof ListCriteria) {
            for (ResourceQueryCriteria q : ((ListCriteria) a).getCriteria()) {
                if (filterPathEquals(q, b)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Add a field that is required for handling the request.  A Repository may implement query projection, retrieving only requested fields.  If a field not requested is required for handling the request, it must also be added to the query for projection.
     */
    public void addRequiredFields(String... beanPaths) {
        this.requiredFields = addPaths(requiredFields, beanPaths);
    }

    public void addRequired(ResourcePath path) {
        this.requiredFields = addPath(requiredFields, path);
    }

    private List<ResourcePath> addPaths(List<ResourcePath> target, String[] beanPaths) {
        List<ResourcePath> result = target;
        if (result == null) {
            result = new ArrayList<ResourcePath>();
        }
        for (String beanPath : beanPaths) {
            result.add(ResourcePath.path(resource, beanPath));
        }
        return result;
    }

    private List<ResourcePath> addPath(List<ResourcePath> target, ResourcePath path) {
        List<ResourcePath> result = target;
        if (result == null) {
            result = new ArrayList<ResourcePath>();
        }
        result.add(path);
        return result;
    }

    /**
     * Add a filter that must always be applied.  For example, a filter that restricts data available to the requestor that may not be passed in the request, but defaulted from an authenticated context.
     */
    public void addCriteria(ResourceQueryCriteria queryCriteria) {
        if (queryCriteria != null) {
            if (criteria == null) {
                criteria = new ArrayList<ResourceQueryCriteria>();
            }
            criteria.add(queryCriteria);
        }
    }

    public void addCriteria(String beanPath, Object value) {
        addCriteria(new ResourcePathFilter<Object>(resource, beanPath, value));
    }

    public void addCriteria(String beanPath, Operator operator, Object value) {
        addCriteria(new ResourcePathFilter<Object>(resource, beanPath, operator, value));
    }

    /**
     * Add a default that should be applied only if not present in the request. For example, if a resource has a delete state, by default it may be desired that only active documents are queried.  However, it might be desirable to also permit access to query deleted documents.
     */
    public void addDefaultCriteria(ResourcePathFilter<?> criteria) {
        if (!isPresentInRequest(criteria)) {
            addCriteria(criteria);
        }
    }

    public void addDefaultCriteria(String beanPath, Object value) {
        addDefaultCriteria(new ResourcePathFilter<Object>(resource, beanPath, value));
    }

    public void addDefaultCriteria(String beanPath, Operator operator, Object value) {
        addDefaultCriteria(new ResourcePathFilter<Object>(resource, beanPath, operator, value));
    }

    /**
     * Set default sort. Ignored if sort is specified by request
     */
    public void setSort(List<ResourceSort> sort) {
        this.sort = sort;
    }

    public List<ResourcePath> getRequiredFields() {
        return requiredFields;
    }

    public List<ResourceQueryCriteria> getCriteria() {
        return criteria;
    }

    public List<ResourceSort> getSort() {
        return sort;
    }

    /**
     * Add default sort by each specified path, with default order (ascending)
     */
    public void setSort(String... beanPaths) {
        List<ResourceSort> sort = new ArrayList<ResourceSort>();
        for (String beanPath : beanPaths) {
            sort.add(new ResourceSort(resource, beanPath));
        }
        setSort(sort);
    }
}
