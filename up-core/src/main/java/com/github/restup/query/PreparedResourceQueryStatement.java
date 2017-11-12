package com.github.restup.query;

import com.github.restup.mapping.fields.MappedField;
import com.github.restup.path.ResourcePath;
import com.github.restup.query.criteria.ResourcePathFilter;
import com.github.restup.query.criteria.ResourcePathFilter.Operator;
import com.github.restup.query.criteria.ResourceQueryCriteria;
import com.github.restup.registry.Resource;
import org.apache.commons.collections4.list.SetUniqueList;

import java.util.*;

import static com.github.restup.util.UpUtils.removeAll;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

public class PreparedResourceQueryStatement extends AbstractResourceQueryStatement {

    private final List<ResourcePath> fields;

    public PreparedResourceQueryStatement(Resource<?, ?> resource, ResourceQueryStatement query,
                                          ResourceQueryDefaults template) {
        super(resource, criteria(query, template), sort(query, template), pagination(query, resource));
        this.fields = queryFields(resource, query, template);
    }

    private static Pagination pagination(ResourceQueryStatement query, Resource<?, ?> resource) {
        if (query != null) {
            return query.getPagination();
        }
        return resource.getDefaultPagination();
    }

    public static List<ResourceQueryCriteria> criteria(ResourceQueryStatement query, ResourceQueryDefaults defaults) {
        return criteria(query, defaults, true);
    }

    public static List<ResourceQueryCriteria> criteria(ResourceQueryStatement query, ResourceQueryDefaults defaults,
                                                       boolean groupEq) {
        List<ResourceQueryCriteria> result = new ArrayList<ResourceQueryCriteria>();
        if (ResourceQueryDefaults.hasCriteria(defaults)) {
            result.addAll(defaults.getCriteria());
        }
        if (ResourceQueryStatement.hasCriteria(query)) {
            if (groupEq) {
                result.addAll(groupEq(query.getRequestedCriteria()));
            } else {
                result.addAll(query.getRequestedCriteria());
            }
        }
        return result;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static List<ResourceQueryCriteria> groupEq(List<ResourceQueryCriteria> requestedCriteria) {
        Map<ResourcePath, Object> equals = new HashMap();
        // create a map of eq/in values by path
        for (ResourceQueryCriteria c : requestedCriteria) {
            if (c instanceof ResourcePathFilter) {
                ResourcePathFilter f = (ResourcePathFilter) c;
                if (Operator.eq == f.getOperator() || Operator.in == f.getOperator()) {
                    Object o = equals.get(f.getPath());
                    equals.put(f.getPath(), addValue(o, f.getValue()));
                }
            }
        }
        //
        List<ResourceQueryCriteria> result = new ArrayList<ResourceQueryCriteria>();
        for (ResourceQueryCriteria c : requestedCriteria) {
            if (c instanceof ResourcePathFilter) {
                ResourcePathFilter f = (ResourcePathFilter) c;
                if (Operator.eq == f.getOperator() || Operator.in == f.getOperator()) {
                    Object value = equals.get(f.getPath());
                    if (value != null) {
                        if (value instanceof Collection) {
                            result.add(new ResourcePathFilter(f.getPath(), Operator.in, value));
                        } else {
                            result.add(new ResourcePathFilter(f.getPath(), Operator.eq, value));
                        }
                        equals.remove(f.getPath());
                    }
                    continue;
                }
            }
            result.add(c);
        }
        return result;
    }

    @SuppressWarnings({"unchecked"})
    private static Object addValue(Object a, Object b) {
        if (a == null) {
            return b;
        }
        List<Object> result;
        if (a instanceof List) {
            result = (List<Object>) a;
        } else {
            result = new ArrayList<Object>();
            addValue(result, a);
        }
        addValue(result, b);
        return result;
    }

    @SuppressWarnings({"unchecked"})
    private static void addValue(List<Object> a, Object b) {
        if (b instanceof Collection) {
            a.addAll((Collection<Object>) b);
        } else {
            a.add(b);
        }
    }

    /**
     * @param query
     * @param defaults
     * @return All of the fields required for
     */
    public static List<ResourcePath> queryFields(Resource<?, ?> resource, ResourceQueryStatement query,
                                                 ResourceQueryDefaults defaults) {
        List<ResourcePath> result = SetUniqueList.setUniqueList(new ArrayList<ResourcePath>());
        result.add(ResourcePath.idPath(resource));
        switch (ResourceQueryStatement.getType(query)) {
            case Default:
                addAll(result, resource.getDefaultSparseFields());
                break;
            case Sparse:
                addAll(result, query.getRequestedPaths());
                break;
            case All:
            case Every:
                addAllPaths(result, resource, false);
        }
        addAndRemove(resource, result, query, false);
        if (defaults != null) {
            // add in fields required for handling request
            addAll(result, defaults.getRequiredFields());
        }
        return result;
    }

    /**
     * @param query
     * @param defaults
     * @return All of the fields required for rendering sparse fields
     */
    public static List<ResourcePath> sparseFields(Resource<?, ?> resource, ResourceQueryStatement query) {
        List<ResourcePath> result = SetUniqueList.setUniqueList(new ArrayList<ResourcePath>());
        switch (ResourceQueryStatement.getType(query)) {
            case Default:
                addAll(result, resource.getDefaultSparseFields());
                break;
            case Sparse:
                addAll(result, query.getRequestedPaths());
                break;
            case All:
                addAllPaths(result, resource, false);
                break;
            case Every:
                addAllPaths(result, resource, true);
        }
        addAndRemove(resource, result, query, true);
        return result;
    }

    private static void addAndRemove(Resource<?, ?> resource, List<ResourcePath> result, ResourceQueryStatement query,
                                     boolean includeTransient) {
        // add relationship paths required which are implied by requested includes
        if (query != null) {
            addAll(result, query.getRequiredRelationshipPaths(), includeTransient);
            // add specific paths added +path
            addAll(result, query.getRequestedPathsAdded(), includeTransient);
        }

        // omit any fields added as restricted
        removeAll(result, resource.getRestrictedFields());

        if (query != null) {
            // remove specific paths requested to be removed -path
            removeAll(result, query.getRequestedPathsExcluded());
        }
    }

    private static void addAllPaths(List<ResourcePath> result, Resource<?, ?> resource, boolean includeTransient) {
        addAll(result, resource.getPaths(includeTransient));
    }

    public static List<ResourceSort> sort(ResourceQueryStatement query, ResourceQueryDefaults defaults) {
        if (defaults != null) {
            if (defaults.getSort() != null && isEmpty(query.getRequestedSort())) {
                return defaults.getSort();
            }
        }
        return query == null ? null : query.getRequestedSort();
    }

    public static void addAll(List<ResourcePath> target, List<ResourcePath> source) {
        if (source != null) {
            for (ResourcePath path : source) {
                // ensure paths are at first position
                target.add(path.first());
            }
        }
    }

    public static void addAll(List<ResourcePath> target, List<ResourcePath> source, boolean includeTransient) {
        if (includeTransient) {
            addAll(target, source);
        } else if (source != null) {
            for (ResourcePath path : source) {
                MappedField<?> mf = path.lastMappedField();
                if (mf != null && !mf.isTransientField()) {
                    target.add(path.first());
                }
            }
        }
    }

    public List<ResourcePath> getFields() {
        return fields;
    }

}
