package com.github.restup.service;

import com.github.restup.annotations.filter.Rank;
import com.github.restup.errors.RequestErrorException;
import com.github.restup.registry.Resource;
import com.github.restup.util.Assert;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link FilterChainContextMethodCommand} which executes annotated filter methods ({@link com.github.restup.annotations.filter.PreCreateFilter}, {@link com.github.restup.annotations.filter.PreUpdateFilter}, {@link com.github.restup.annotations.filter.PostCreateFilter}, etc).
 *
 * {@link AnnotatedFilterMethodCommand} are ranked so that they can be executed in a consistent, configurable order.
 */
public class AnnotatedFilterMethodCommand extends FilterChainContextMethodCommand {

    private final static Logger log = LoggerFactory.getLogger(AnnotatedFilterMethodCommand.class);

    private final Resource<?, ?> resource;
    private final int rank;

    public AnnotatedFilterMethodCommand(Resource<?, ?> resource, Object objectInstance, Class<? extends Annotation> methodAnnotation, Method method, Object[] arguments) {
        super(objectInstance, methodAnnotation, method, arguments);
        Assert.notNull(resource, "A resource instance is required");
        this.resource = resource;
        rank = getRank(getMethod());
    }

    private static int getRank(Method method) {
        Rank rank = method == null ? null : method.getAnnotation(Rank.class);
        return rank == null ? 0 : rank.value();
    }

    @Override
    protected void debug(Object[] params) {
        log.debug("Executing {} ranked {} for {} resource {}.{}(...)", getLabel(), rank,
            resource, getObjectInstance().getClass(), getMethod().getName());
    }

    @Override
    protected RuntimeException handle(Exception t) {
        return RequestErrorException.of(resource, t);
    }

    public int getRank() {
        return rank;
    }

    public Resource<?, ?> getResource() {
        return resource;
    }

}
