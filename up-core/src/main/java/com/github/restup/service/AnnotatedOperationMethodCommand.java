package com.github.restup.service;

import com.github.restup.annotations.filter.PreCreateFilter;
import com.github.restup.annotations.filter.PreUpdateFilter;
import com.github.restup.annotations.filter.Validation;
import com.github.restup.annotations.operations.AutoWrapDisabled;
import com.github.restup.bind.MethodArgumentFactory;
import com.github.restup.errors.ErrorFactory;
import com.github.restup.path.ResourcePath;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.service.model.response.ResourceResultConverter;
import com.github.restup.service.model.response.ResourceResultConverterFactory;
import com.github.restup.util.ReflectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Executes a service method composed of "pre-filters", a operations method, and "post-filters"
 * Both the pre and post filter chain are assembled from annotated methods.
 *
 * @author abuttaro
 */
public class AnnotatedOperationMethodCommand implements MethodCommand<Object> {

    //TODO FilteredServiceMethodCommand may inherit this?

    private final AnnotatedRepositoryMethodCommand repositoryMethod;
    private final Class<?>[] argumentTypes;
    private final Resource<?, ?> resource;
    private final ErrorFactory errorFactory;
    private final MethodArgumentFactory argumentFactory;
    private final ResourceResultConverter resultConverter;

    /**
     * @param resource
     * @param objectInstance   The operations instance from which the repositorMethod is executed
     * @param repositoryMethod The operations method
     * @param repoAnnotation   The annotation of the operations method.
     */
    public AnnotatedOperationMethodCommand(Resource<?, ?> resource
            , Object objectInstance
            , Method repositoryMethod
            , Class<? extends Annotation> repoAnnotation) {
        this.resource = resource;
        ResourceRegistry registry = resource.getRegistry();
        this.errorFactory = registry.getSettings().getErrorFactory();
        this.argumentFactory = registry.getSettings().getMethodArgumentFactory();

        boolean disableAutoWrap = ReflectionUtils.isAutoWrapDisabled(repositoryMethod);

        // build the set of unique method argument types
        MethodArgumentsBuilder builder = new MethodArgumentsBuilder();
        builder.addArgument(repositoryMethod);
        argumentTypes = builder.build();

        this.repositoryMethod = new AnnotatedRepositoryMethodCommand(resource, objectInstance, repoAnnotation, repositoryMethod, argumentTypes);
        this.resultConverter = disableAutoWrap ? new ResourceResultConverterFactory.NoOpResourceResultConverter() :
                ResourceResultConverterFactory.getInstance().getConverter(repoAnnotation);
    }

    public final Object execute(Object... state) {
        // build up a context for method execution
        FilterChainContext ctx = context(state);

        // execute operations method & add result to context
        Object result = repositoryMethod.execute(ctx);

        // convert to appropriate response type
        Object converted = resultConverter.convert(result);
        if ( converted != result ) {
            result = converted;
            ctx.addArgument(result);
        }

        return result;
    }


    private FilterChainContext context(Object... state) {
        FilterChainContext ctx = new FilterChainContext(argumentFactory, errorFactory, argumentTypes);
        ctx.addArguments(resource, resource.getRegistry());
        ctx.addArgument(state);
        return ctx;
    }

}
