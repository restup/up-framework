package com.github.restup.repository.collections;

import com.github.restup.annotations.operations.CreateResource;
import com.github.restup.annotations.operations.DeleteResource;
import com.github.restup.annotations.operations.ListResource;
import com.github.restup.annotations.operations.ReadResource;
import com.github.restup.annotations.operations.UpdateResource;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.path.ResourcePath;
import com.github.restup.query.Pagination;
import com.github.restup.query.PreparedResourceQueryStatement;
import com.github.restup.query.ResourceSort;
import com.github.restup.query.criteria.ResourceQueryCriteria;
import com.github.restup.registry.Resource;
import com.github.restup.service.model.request.CreateRequest;
import com.github.restup.service.model.request.DeleteRequest;
import com.github.restup.service.model.request.ReadRequest;
import com.github.restup.service.model.request.UpdateRequest;
import com.github.restup.service.model.response.PagedResult;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

/**
 * A repository backed by a {@link Map}
 */
public class MapBackedRepository<T, ID extends Serializable> {

    private final IdentityStrategy<ID> identityStrategy;
    private final Map<ID, T> map;

    public MapBackedRepository(IdentityStrategy<ID> identityStrategy, Map<ID, T> map) {
        this.identityStrategy = identityStrategy;
        this.map = map;
    }

    public MapBackedRepository(IdentityStrategy<ID> identityStrategy) {
        this(identityStrategy, new ConcurrentHashMap<>());
    }

    @CreateResource
    public T create(Resource<?, ?> resource, CreateRequest<T> request) {
        T t = request.getData();
        //TODO composite keys
        MappedField<ID> idField = (MappedField) resource.getIdentityField()[0];
        ID id = idField.readValue(t);
        if (id == null) {
            id = identityStrategy.getNextId();
            idField.writeValue(t, id);
        }
        map.put(id, t);
        return t;
    }

    @ReadResource
    public T find(ReadRequest<T, ID> request) {
        return find(request.getId());
    }

    public T find(ID id) {
        return map.get(id);
    }

    @DeleteResource
    public T delete(DeleteRequest<T, ID> request) {
        return map.remove(request.getId());
    }

    @UpdateResource
    public T update(Resource<?, ?> resource, UpdateRequest<T, ID> request) {
        // PersistenceResult<T>
        T existing = find(request.getId());
        T update = request.getData();
        if (update != null && request.getRequestedPaths() != null) {
            for (ResourcePath path : request.getRequestedPaths()) {
                Object value = path.getValue(update);
                path.setValue(existing, value);
            }
        }
        return existing;
    }

    @ListResource
    public PagedResult<T> list(PreparedResourceQueryStatement ps) {
        List<T> result = new ArrayList<>(map.values());

        Pagination paging = ps.getPagination();

        result = filter(ps, result);

        Long totalCount = null;

        if (paging.isWithTotalsEnabled()) {
            totalCount = Long.valueOf(result.size());
        }

        sort(ps, result);

        if (!paging.isPagingEnabled()) {
            paging = null;
        } else {
            Integer limit = paging.getLimit();
            if (Pagination.isPagedListRequired(paging, totalCount)) {
                int start = Pagination.getStart(paging);

                result = result.stream()
                        .skip(start)
                        .limit(limit)
                        .collect(Collectors.toList());
            } else if (limit > 0) {
                result = Collections.EMPTY_LIST;
            } else {
                paging = null;
                result = null;
            }
        }

        return PagedResult.of(result, paging, totalCount);
    }

    private List<T> filter(PreparedResourceQueryStatement ps, List<T> result) {
        List<ResourceQueryCriteria> criteria = ps.getRequestedCriteria();
        if (CollectionUtils.isNotEmpty(criteria)) {
            return result
                    .stream()
                    .filter(t -> filter(criteria, t))
                    .collect(Collectors.toList());
        }
        return result;
    }

    private boolean filter(List<ResourceQueryCriteria> list, T t) {
    		return ! list.stream()
    				.filter(c -> !c.filter(t))
    				.findAny()
    				.isPresent();
    }

    private void sort(PreparedResourceQueryStatement ps, List<T> result) {
        List<ResourceSort> sortFields = ps.getRequestedSort();
        if (sortFields == null) {
            sortFields = Arrays.asList(ResourceSort.of(ResourcePath.idPath(ps.getResource())));
        }
        Collections.sort(result, new Sort<>(sortFields));
    }

    private static class Sort<T> implements Comparator<T> {

        final List<ResourceSort> sortFields;

        private Sort(List<ResourceSort> sortFields) {
            this.sortFields = sortFields;
        }

        @Override
        public int compare(T a, T b) {
            for (ResourceSort sort : sortFields) {
                ResourcePath path = sort.getPath();
                Comparable<Object> x = (Comparable) path.getValue(a);
                Comparable<Object> y = (Comparable) path.getValue(b);
                int i = x.compareTo(y);
                if (i != 0) {
                    return sort.isAscending() ? i : -i;
                }
            }
            return 0;
        }
    }
}
