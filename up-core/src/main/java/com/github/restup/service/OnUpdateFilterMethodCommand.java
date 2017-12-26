package com.github.restup.service;

import com.github.restup.annotations.filter.Validation;
import com.github.restup.path.ResourcePath;
import com.github.restup.query.ResourceQuery;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.service.model.request.PersistenceRequest;
import com.github.restup.service.model.request.UpdateRequest;
import com.github.restup.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * {@link AnnotatedFilterMethodCommand} for {@link Validation} annotated filter methods handling update operations only.
 *
 * When fields absent in the request are required for a validation, persisted state will be merged with request state so that validations may be performed correctly.
 */
public class OnUpdateFilterMethodCommand extends OnCreateFilterMethodCommand {

    private final static Logger log = LoggerFactory.getLogger(OnUpdateFilterMethodCommand.class);

    private final ResourceRegistry registry;

    public OnUpdateFilterMethodCommand(Resource<?, ?> resource, Object objectInstance, Validation validation, Method method, Object[] arguments) {
        super(resource, objectInstance, validation, method, arguments);
        this.registry = resource.getRegistry();
    }

    /**
     * Only validate path if the path is included in the PersistenceRequest
     */
    @Override
    protected boolean isValidatePath(FilterChainContext ctx, ResourcePath path) {
        boolean result = ctx.getPersistenceRequest().hasPath(path);
        if (result && ctx.getPersistedState() == null) {
            mergePersistedState(ctx);
        }
        return result;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T, ID extends Serializable> void mergePersistedState(FilterChainContext ctx) {
        // before we attempt to load & merge persisted state, check if the required validation
        // paths are absent from the request
        if (isMissingValidationPaths(ctx.getPersistenceRequest())) {
            // load persisted state
            T persisted = (T) loadResourceById(ctx);
            if (persisted != null) {
                // add to context so that future validations do not have to load
                // not really required with level 1 cache
                ctx.setPersistedState(persisted);

                Resource<T, ID> resource = (Resource) getResource();
                UpdateRequest<T, ID> request = (UpdateRequest) ctx.getPersistenceRequest();
                ID id = request.getId();
                log.debug("merging persisted state with request state for {} {}", resource, id);

                // apply all of missing request values with persisted state.
                T requested = request.getData();
                for (ResourcePath path : getPaths()) {
                    if (isMergeAtPath(request, path)) {
                        Object persistedValue = path.getValue(persisted);
                        if (persistedValue != null) {
                            path.setValue(requested, persistedValue);
                        }
                    }
                }
            }
        }
    }

    private <T, ID extends Serializable> boolean isMissingValidationPaths(PersistenceRequest<T> request) {
        if (request != null) {
            Assert.notNull(request.getData(), "request data must not be null");
            T requested = request.getData();
            for (ResourcePath path : getPaths()) {
                if (null == path.getValue(requested)) {
                    return true;
                }
            }
        }
        return false;
    }

    private <T, ID extends Serializable> boolean isMergeAtPath(PersistenceRequest<T> request, ResourcePath path) {
        if (!request.hasPath(path)) {
            // request does not have path at all
            return true;
        }
        /// TODO do we need to consider partial updates?
        // The request may contain a path, but that may not support partial update
        // if so, we may have to merge the persisted state in

//		! request.hasPath(path) || (validation..isPartialUpdateSupported() && !request.hasField(path) ) 
        return false;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Object loadResourceById(FilterChainContext ctx) {
        // TODO consider bulk updates... here we are fetching 1 at a time
        // with bulk updates, we will want to fetch all at once.
        if (ctx.getPersistedState() != null) {
            // persisted state is already loaded and in context
            return ctx.getPersistedState();
        }
        UpdateRequest request = (UpdateRequest) ctx.getPersistenceRequest();
        Object data = getData(request);
        if (data != null) {
            return ResourceQuery.find(registry, request.getResource().getName(), request.getId());
        }
        return null;
    }

    private <T> T getData(PersistenceRequest<T> request) {
        return request == null ? null : request.getData();
    }
}
