package com.github.restup.service;

import com.github.restup.annotations.filter.Validation;
import com.github.restup.path.ResourcePath;
import com.github.restup.registry.Resource;
import com.github.restup.service.model.request.PersistenceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * {@link AnnotatedFilterMethodCommand} for {@link Validation} annotated methods handling create operations only.
 */
public class OnCreateFilterMethodCommand extends AnnotatedFilterMethodCommand {

    private final static Logger log = LoggerFactory.getLogger(OnCreateFilterMethodCommand.class);

    private final Validation validation;
    private final ResourcePath[] paths;

    public OnCreateFilterMethodCommand(Resource<?, ?> resource, Object objectInstance, Validation validation, Method method, Object[] arguments) {
        super(resource, objectInstance, validation.getClass(), method, arguments);
        this.validation = validation;
        ResourcePath[] paths = new ResourcePath[validation.path().length];
        for (int i = 0; i < paths.length; i++) {
            paths[i] = ResourcePath.path(resource, validation.path()[i]);
        }
        this.paths = paths;
    }

    @Override
    public Object execute(Object... args) {
        FilterChainContext ctx = (FilterChainContext) args[0];
        if (validation.skipOnErrors() && ctx.hasErrors()) {
            log.debug("Skip Validation (hasErrors()) ranked {} for {} resource {}.{}(...)", getRank(), getResource(), getObjectInstance().getClass(), getMethod().getName());
            return null;
        }
        PersistenceRequest<?> request = ctx.getPersistenceRequest();
        if (request != null) { // should never be null
            for (ResourcePath path : paths) {
                if (isValidatePath(ctx, path)) {
                    ctx.addArgument(path);
                    return super.execute(args);
                }
            }
        }
        log.debug("Skip Validation (n/a) ranked {} for {} resource {}.{}(...)", getRank(), getResource(), getObjectInstance().getClass(), getMethod().getName());
        return null;
    }

    protected boolean isValidatePath(FilterChainContext ctx, ResourcePath path) {
        if (validation.required()) {
            // always execute if required.
            // a field may not be required, but could have a validation if present
            return true;
        }
        // otherwise validate if the field is present in the request
        Object value = path.getValue(ctx.getPersistenceRequest().getData());
        return value != null;
    }

    public ResourcePath[] getPaths() {
        return paths;
    }

    public Validation getValidation() {
        return validation;
    }
}
