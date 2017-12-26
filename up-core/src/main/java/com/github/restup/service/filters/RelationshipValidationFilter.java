package com.github.restup.service.filters;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.restup.annotations.filter.PreCreateFilter;
import com.github.restup.annotations.filter.PreUpdateFilter;
import com.github.restup.errors.ErrorBuilder;
import com.github.restup.errors.Errors;
import com.github.restup.mapping.fields.IterableField;
import com.github.restup.path.ResourcePath;
import com.github.restup.query.ResourceQuery;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.service.model.request.CreateRequest;
import com.github.restup.service.model.request.UpdateRequest;

public class RelationshipValidationFilter {

    //TODO when created or updated references to other resources should be validated.

    @PreCreateFilter
    public <T, ID extends Serializable> void validateReferencesOnCreate(ResourceRegistry registry, Errors errors, CreateRequest<T> request) {
        validateReferences(registry, errors, request.getData(), Resource.getAllPaths(request.getResource()));
    }

    @PreUpdateFilter
    public <T, ID extends Serializable> void validateReferencesOnUpdate(ResourceRegistry registry, Errors errors, UpdateRequest<T, ID> request) {
        validateReferences(registry, errors, request.getData(), request.getRequestedPaths());
    }

    private <T, ID extends Serializable> void validateReferences(ResourceRegistry registry, Errors errors, T data,
            List<ResourcePath> requestedPaths) {
        Map<String, Set<Object>> idsByRelationship = getIdsByRelationship(registry, requestedPaths, data);
        removeValidRelationshipsFromMap(registry, idsByRelationship);
        for (Entry<String, Set<Object>> e : idsByRelationship.entrySet()) {
            Set<Object> invalidIds = e.getValue();
            if (CollectionUtils.isNotEmpty(invalidIds)) {
                Map<ResourcePath, Set<Object>> idsByPath = getIdsByPath(registry, e.getKey(), requestedPaths, data);
                for (Entry<ResourcePath, Set<Object>> ee : idsByPath.entrySet()) {
                    Set<Object> idsRequested = ee.getValue();
                    if (CollectionUtils.isNotEmpty(idsRequested)) {
                        Collection<Object> invalidIdsAtPath = CollectionUtils.intersection(invalidIds, idsRequested);
                        if (!invalidIdsAtPath.isEmpty()) {
                            ResourcePath path = ee.getKey();
                            String apiPath = path.getApiPath();
                            String resourceName = path.getResource().getName();
                            Object idMeta = singularOrPluralAppropriate(path, invalidIdsAtPath);
                            errors.addError(ErrorBuilder.builder(path)
                                    .codeSuffix("NOT_FOUND")
                                    .title("Reference not found")
                                    .detail("{0} {1} not found", StringUtils.capitalize(resourceName), apiPath)
                                    .meta(path.getApiPath(), idMeta)
                                    .meta("referencedResource", registry.getResource(e.getKey()).getName())
                            );
                        }
                    }
                }
            }
        }
    }

    private Object singularOrPluralAppropriate(ResourcePath path, Collection<Object> invalidIdsAtPath) {
        if (!(path.lastMappedField() instanceof IterableField)) {
            if (CollectionUtils.size(invalidIdsAtPath) == 1) {
                return invalidIdsAtPath.iterator().next();
            }
        }
        return invalidIdsAtPath;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void removeValidRelationshipsFromMap(ResourceRegistry registry, Map<String, Set<Object>> idsByRelationship) {
        for (Entry<String, Set<Object>> e : idsByRelationship.entrySet()) {
            Set<Object> idsToValidate = e.getValue();
            if (CollectionUtils.isNotEmpty(idsToValidate)) {
                Resource<?, ?> toResource = registry.getResource(e.getKey());
                if (toResource != null) {
                    // TODO projection?
                    List result = ResourceQuery.query(toResource)
                            .filterById(e.getValue()).list();
                    Set idsFound = Resource.getIds(toResource, result);
                    idsToValidate.removeAll(idsFound);
                }
            }
        }
    }

    /**
     * Get a list of ids by relationship for validation query by id
     */
    private Map<String, Set<Object>> getIdsByRelationship(ResourceRegistry registry, List<ResourcePath> requestedPaths, Object data) {
        Map<String, Set<Object>> idsByRelationship = new HashMap<>();
        for (ResourcePath path : requestedPaths) {
            String relationshipName = ResourcePath.getRelationshipResource(registry, path);
            if (relationshipName != null) {
                Set<Object> ids = idsByRelationship.get(relationshipName);
                if (ids == null) {
                    ids = new HashSet<Object>();
                    idsByRelationship.put(relationshipName, ids);
                }
                path.collectValues(ids, data);
            }
        }
        return idsByRelationship;
    }

    /**
     * If there are errors to report detail we need to know which specific paths had invalid values
     *
     * @return Map of relationship ids by path
     */
    private Map<ResourcePath, Set<Object>> getIdsByPath(ResourceRegistry registry, String forRelationship, List<ResourcePath> requestedPaths, Object data) {
        Map<ResourcePath, Set<Object>> idsByPath = new HashMap<ResourcePath, Set<Object>>();
        for (ResourcePath path : requestedPaths) {
            String relationshipClass = ResourcePath.getRelationshipResource(registry, path);
            if ( Objects.equals(relationshipClass, forRelationship)) {
                Set<Object> ids = new HashSet<Object>();
                path.collectValues(ids, data);
                idsByPath.put(path, ids);
            }
        }
        return idsByPath;
    }

}
