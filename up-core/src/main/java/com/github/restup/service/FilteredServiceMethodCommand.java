package com.github.restup.service;
import static com.github.restup.service.model.response.ResourceResultConverterFactory.*;
import com.github.restup.annotations.filter.PreCreateFilter;
import com.github.restup.annotations.filter.PreUpdateFilter;
import com.github.restup.annotations.filter.Validation;
import com.github.restup.annotations.operations.AutoWrapDisabled;
import com.github.restup.bind.MethodArgumentFactory;
import com.github.restup.bind.converter.NoOpConverter;
import com.github.restup.errors.ErrorFactory;
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
import java.util.function.Function;

/**
 * Executes a service method composed of "pre-filters", a operations method, and "post-filters"
 * Both the pre and post filter chain are assembled from annotated methods.
 *
 * @author abuttaro
 */
class FilteredServiceMethodCommand implements MethodCommand<Object> {
    private final AnnotatedRepositoryMethodCommand repositoryMethod;
    private final Class<?>[] argumentTypes;
    private final Resource<?, ?> resource;
    private final ErrorFactory errorFactory;
    private final MethodArgumentFactory argumentFactory;
    private final ResourceResultConverter resultConverter;
    private AnnotatedFilterMethodCommand[] preFilters;
    private AnnotatedFilterMethodCommand[] postFilters;


    /**
     * @param resource
     * @param objectInstance   The operations instance from which the repositorMethod is executed
     * @param repositoryMethod The operations method
     * @param repoAnnotation   The annotation of the operations method.
     * @param preAnnotation    The annotation used for pre repositoryMethod execution
     * @param postAnnotation   The annotation used for post repositoryMethod execution
     * @param filters          Objects containing annotated filter methods
     */
    FilteredServiceMethodCommand(Resource<?, ?> resource
            , Object objectInstance
            , Method repositoryMethod
            , Class<? extends Annotation> repoAnnotation
            , Class<? extends Annotation> preAnnotation
            , Class<? extends Annotation> postAnnotation
            , Object... filters) {
        this.resource = resource;
        ResourceRegistry registry = resource.getRegistry();
        this.errorFactory = registry.getSettings().getErrorFactory();
        this.argumentFactory = registry.getSettings().getMethodArgumentFactory();

        // collect method detail for pre/post annotations
        FiltersBuilder filtersBuilder = new FiltersBuilder(resource, preAnnotation, postAnnotation);
        filtersBuilder.addFilters(filters);

        boolean disableAutoWrap = ReflectionUtils.isAutoWrapDisabled(repositoryMethod);

        // build the set of unique method argument types
        MethodArgumentsBuilder builder = new MethodArgumentsBuilder();
        builder.addArguments(filtersBuilder.getMethods());
        builder.addArgument(repositoryMethod);
        argumentTypes = builder.build();

        // now build the filters with the argument type detail
        filtersBuilder.argumentTypes = argumentTypes;

        this.preFilters = filtersBuilder.buildPreFilters();
        this.postFilters = filtersBuilder.buildPostFilters();
        this.repositoryMethod = new AnnotatedRepositoryMethodCommand(resource, objectInstance, repoAnnotation, repositoryMethod, argumentTypes);
        this.resultConverter = disableAutoWrap ? new NoOpResourceResultConverter() :
                ResourceResultConverterFactory.getInstance().getConverter(repoAnnotation);
    }

    public final Object execute(Object... state) {
        // build up a context for method execution
        FilterChainContext ctx = context(state);
        // pre filters
        executeFilters(preFilters, ctx);
        ctx.assertErrors();

        // execute operations method & add result to context
        Object result = repositoryMethod.execute(ctx);
        ctx.addArgument(result);

        // convert to appropriate response type
        Object converted = resultConverter.convert(result);
        if ( converted != result ) {
            result = converted;
            ctx.addArgument(result);
        }

        ctx.assertErrors();
        // execute post filters
        executeFilters(postFilters, ctx);
        ctx.assertErrors();
        return result;
    }


    private FilterChainContext context(Object... state) {
        FilterChainContext ctx = new FilterChainContext(argumentFactory, errorFactory, argumentTypes);
        ctx.addArguments(resource, resource.getRegistry());
        ctx.addArgument(state);
        return ctx;
    }

    private void executeFilters(AnnotatedFilterMethodCommand[] filters, FilterChainContext ctx) {
        for (AnnotatedFilterMethodCommand cmd : filters) {
            Object result = cmd.execute(ctx);
            ctx.addArgument(result);
        }
    }

    public final static class FilterOrderComparator implements Comparator<AnnotatedFilterMethodCommand> {
        public int compare(AnnotatedFilterMethodCommand a, AnnotatedFilterMethodCommand b) {
            if (b == null) return -1;
            if (a == null) return 1;
            if (a.getRank() == b.getRank()) {
                return a.getMethod().getName().compareTo(b.getMethod().getName());
            }
            return a.getRank() < b.getRank() ? -1 : 1;
        }

    }

    /**
     * Build filter methods, looking for pre/post annotations
     *
     * @author abuttaro
     */
    private final class FiltersBuilder {
        private final Resource<?, ?> resource;
        private final List<Pair<Object, Method>> preMethods;
        private final List<Pair<Object, Method>> postMethods;
        private final List<Pair<Object, Method>> validations;
        private final Class<? extends Annotation> preAnnotation;
        private final Class<? extends Annotation> postAnnotation;
        private final boolean create;
        private final boolean update;
        private final Comparator<? super AnnotatedFilterMethodCommand> comparator;

        private Class<?>[] argumentTypes;

        private FiltersBuilder(Resource<?, ?> resource, Class<? extends Annotation> preAnnotation,
                               Class<? extends Annotation> postAnnotation) {
            this.resource = resource;
            this.preAnnotation = preAnnotation;
            this.postAnnotation = postAnnotation;
            this.create = preAnnotation == PreCreateFilter.class;
            this.update = preAnnotation == PreUpdateFilter.class;
            this.validations = create || update ? new ArrayList<Pair<Object, Method>>() : null;
            this.preMethods = new ArrayList<Pair<Object, Method>>();
            this.postMethods = new ArrayList<Pair<Object, Method>>();
            this.comparator = new FilterOrderComparator();
        }

        @SuppressWarnings("unchecked")
        public List<Method> getMethods() {
            List<Method> methods = new ArrayList<Method>();
            appendMethods(methods, preMethods, postMethods, validations);
            return methods;
        }

        private void appendMethods(List<Method> methods, List<Pair<Object, Method>>... lists) {
            for (List<Pair<Object, Method>> list : lists) {
                if (list != null) {
                    for (Pair<Object, Method> p : list) {
                        methods.add(p.getValue());
                    }
                }
            }
        }

        public void addFilters(Object[] filters) {
            if (filters != null) {
                for (Object filter : filters) {
                    addFilter(filter);
                }
            }
        }

        public void addFilters(Iterable<Object> filters) {
            if (filters != null) {
                for (Object filter : filters) {
                    addFilter(filter);
                }
            }
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private void addFilter(Object filter) {
            if (filter != null) {
                if (filter instanceof Iterable) {
                    addFilters((Iterable) filter);
                } else if (filter instanceof Object[]) {
                    addFilters((Object[]) filter);
                } else {
                    if (filter instanceof ServiceFilter) {
                        ServiceFilter serviceFilter = (ServiceFilter) filter;
                        if (!serviceFilter.accepts(this.resource)) {
                            return;
                        }
                    }
                    Method[] methods = filter.getClass().getMethods();
                    for (Method m : methods) {
                        Annotation pre = m.getAnnotation(preAnnotation);
                        if (pre != null) {
                            this.preMethods.add(pair(filter, m));
                        }
                        Annotation post = m.getAnnotation(postAnnotation);
                        if (post != null) {
                            this.postMethods.add(pair(filter, m));
                        }
                        if (create || update) {
                            Validation validation = m.getAnnotation(Validation.class);
                            if (validation != null) {
                                validations.add(pair(filter, m));
                            }
                        }
                    }
                }
            }
        }

        private Pair<Object, Method> pair(Object filter, Method m) {
            return new ImmutablePair<Object, Method>(filter, m);
        }

        /**
         * Builds a AnnotatedFilterMethodCommand for all {@link #preMethods} found with {@link #preAnnotation}.
         * For {@link #validations} found, builds either an {@link OnCreateFilterMethodCommand} or {@link OnUpdateFilterMethodCommand}
         * as appropriate
         *
         * @return
         */
        public AnnotatedFilterMethodCommand[] buildPreFilters() {
            List<AnnotatedFilterMethodCommand> result = new ArrayList<AnnotatedFilterMethodCommand>();
            for (Pair<Object, Method> p : preMethods) {
                result.add(new AnnotatedFilterMethodCommand(resource, p.getKey(), preAnnotation, p.getValue(), argumentTypes));
            }
            if (validations != null) {
                for (Pair<Object, Method> p : validations) {
                    Validation validation = p.getValue().getAnnotation(Validation.class);
                    if (create && validation.onCreate()) {
                        result.add(new OnCreateFilterMethodCommand(resource, p.getKey(), validation, p.getValue(), argumentTypes));
                    }
                    if (update && validation.onUpdate()) {
                        result.add(new OnUpdateFilterMethodCommand(resource, p.getKey(), validation, p.getValue(), argumentTypes));
                    }
                }
            }
            return build(result);
        }

        /**
         * Builds a AnnotatedFilterMethodCommand for all {@link #postMethods} found with {@link #postAnnotation}
         *
         * @return
         */
        public AnnotatedFilterMethodCommand[] buildPostFilters() {
            List<AnnotatedFilterMethodCommand> result = new ArrayList<AnnotatedFilterMethodCommand>();
            for (Pair<Object, Method> p : postMethods) {
                result.add(new AnnotatedFilterMethodCommand(resource, p.getKey(), postAnnotation, p.getValue(), argumentTypes));
            }
            return build(result);
        }

        private AnnotatedFilterMethodCommand[] build(List<AnnotatedFilterMethodCommand> result) {
            Collections.sort(result, comparator);
            return result.toArray(new AnnotatedFilterMethodCommand[result.size()]);
        }
    }
}
