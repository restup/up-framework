package com.github.restup.service;

import com.github.restup.errors.RequestErrorException;
import com.github.restup.registry.Resource;
import com.github.restup.util.Assert;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotatedRepositoryMethodCommand extends FilterChainContextMethodCommand {

    private final static Logger log = LoggerFactory.getLogger(AnnotatedRepositoryMethodCommand.class);

    private final Resource<?, ?> resource;

    public AnnotatedRepositoryMethodCommand(Resource<?, ?> resource, Object objectInstance, Class<? extends Annotation> methodAnnotation, Method method, Object[] arguments) {
        super(objectInstance, methodAnnotation, method, arguments);
        Assert.notNull(resource, "A resource instance is required");
        this.resource = resource;
    }

    @Override
    protected RuntimeException handle(Exception t) {
        return RequestErrorException.of(resource, t);
    }

    @Override
    protected void debug(Object[] params) {
        log.debug("Executing {} method for {} resource {}.{}(...)", getLabel(), resource, getObjectInstance().getClass(), getMethod().getName());
    }

}
