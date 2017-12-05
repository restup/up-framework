package com.github.restup.service.filters;

import com.github.restup.annotations.filter.PostCreateFilter;
import com.github.restup.annotations.filter.PostDeleteFilter;
import com.github.restup.annotations.filter.PostListFilter;
import com.github.restup.annotations.filter.PostReadFilter;
import com.github.restup.annotations.filter.PostUpdateFilter;
import com.github.restup.query.ResourceQuery;
import com.github.restup.query.ResourceQuery.Builder;
import com.github.restup.query.ResourceQueryStatement;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.registry.ResourceRelationship;
import com.github.restup.service.model.request.QueryRequest;
import com.github.restup.service.model.response.ReadResult;
import com.github.restup.service.model.response.ResourceResult;
import java.util.List;
import java.util.Set;

public class IncludeFilter {

    //TODO when includes are requested, add includes to results

    @PostCreateFilter
    @PostUpdateFilter
    @PostDeleteFilter
    @PostReadFilter
    @PostListFilter
    public <T> void queryIncluded(ResourceRegistry registry, Resource<?, ?> resource, QueryRequest request, ResourceResult<T> result) {
        List<ResourceQueryStatement> queries = request.getSecondaryQueries();
        for (ResourceQueryStatement query : queries) {
            ResourceRelationship<?, ?, ?, ?> relationship = registry.getRelationship(resource, query.getResource());
            if (relationship != null) {
                Set<?> ids = relationship.getJoinIds(resource, result.getData());
                Builder<Object> builder = ResourceQuery.query(query);
                if (relationship.isFrom(resource)) {
                    builder.filter(relationship.getToPaths(), ids);
                } else {
                    builder.filter(relationship.getFromPaths(), ids);
                }
                ReadResult<List<Object>> includeResult = builder.result();
//				result.addIncludeResult(includeResult);
            }
        }
    }

}
