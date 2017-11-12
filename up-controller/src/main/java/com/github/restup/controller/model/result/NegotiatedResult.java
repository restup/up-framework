package com.github.restup.controller.model.result;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.path.PathValue;
import com.github.restup.path.ResourcePath;
import com.github.restup.query.PreparedResourceQueryStatement;
import com.github.restup.query.ResourceQueryStatement;
import com.github.restup.registry.Resource;
import com.github.restup.service.model.ResourceData;
import com.github.restup.service.model.response.PagedResult;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class NegotiatedResult {

    private final ParsedResourceControllerRequest<?> request;
    private final Object result;
    private final Map<Resource<?, ?>, Map<PathValue, Map<PathValue, ?>>> mappedPaths;

    protected NegotiatedResult(ParsedResourceControllerRequest<?> request, Object result) {
        super();
        this.request = request;
        this.result = result;
        mappedPaths = map();
    }

    public boolean isCount() {
        if (result instanceof PagedResult) {
            PagedResult<?> paged = (PagedResult<?>) result;
            return paged.getData() == null
                    && paged.getLimit() == null
                    && paged.getOffset() == null
                    && paged.getTotal() != null;
        }
        return false;
    }

    protected Map<Resource<?, ?>, Map<PathValue, Map<PathValue, ?>>> map() {
        Map<Resource<?, ?>, Map<PathValue, Map<PathValue, ?>>> result = new HashMap<Resource<?, ?>, Map<PathValue, Map<PathValue, ?>>>();
        for (ResourceQueryStatement query : request.getRequestedQueries()) {
            map(result, query.getResource());
        }
        if (!result.containsKey(request.getResource())) {
            map(result, request.getResource());
        }
        return result;
    }

    protected void map(Map<Resource<?, ?>, Map<PathValue, Map<PathValue, ?>>> result, Resource<?, ?> resource) {
        List<ResourcePath> sparseFields = getSparseFields(resource);
        result.put(resource, map(sparseFields));
    }

    private Map<PathValue, Map<PathValue, ?>> map(List<ResourcePath> sparseFields) {
        // use LinkedHashMap for insertion ordered keys
        Map<PathValue, Map<PathValue, ?>> map = new LinkedHashMap<PathValue, Map<PathValue, ?>>();
        for (ResourcePath path : sparseFields) {
            map(map, path.firstMappedFieldPath());
        }
        return map;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void map(Map<PathValue, Map<PathValue, ?>> map, ResourcePath path) {
        PathValue pv = path.value();
        if (!pv.isReservedPath()) {
            ResourcePath next = path.next();
            if (next != null) {
                // TODO consider Indexed paths?
                // TODO if path is resource get its default paths
                // TODO default fields for MappedClass
                Map<PathValue, Map<PathValue, ?>> existing = (Map) map.get(pv);
                if (existing == null) {
                    existing = new HashMap<PathValue, Map<PathValue, ?>>();
                    map.put(pv, existing);
                }
                map(existing, next);
            } else {
                map.put(pv, null);
            }
        }
    }

    protected List<ResourcePath> getSparseFields(Resource<?, ?> resource) {
        ResourceQueryStatement query = ResourceQueryStatement
                .getQuery(resource, request.getRequestedQueries());
        return PreparedResourceQueryStatement.sparseFields(resource, query);
    }

    public Resource<?, ?> getResource() {
        return request.getResource();
    }

    @SuppressWarnings("rawtypes")
    public Object getData() {
        if (result instanceof ResourceData) {
            return ((ResourceData) result).getData();
        }
        return result;
    }

    public Map<PathValue, Map<PathValue, ?>> getMappedPaths() {
        return getMappedPaths(getResource());
    }

    public Map<PathValue, Map<PathValue, ?>> getMappedPaths(Resource<?, ?> resource) {
        return mappedPaths.get(resource);
    }

    public ParsedResourceControllerRequest<?> getRequest() {
        return request;
    }

    public Object getResult() {
        return result;
    }

}
