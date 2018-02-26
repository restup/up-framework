package com.github.restup.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.restup.errors.RequestError;
import com.github.restup.registry.Resource;
import com.github.restup.util.Assert;

/**
 * {@link MethodCommand} which always throws an {@link RequestErrorException} indicating that the operation is not supported.  Serves as a placeholder for services with missing operations
 *
 * @author abuttaro
 */
public class UnsupportedMethodCommand implements MethodCommand<Object> {

    private final static Logger log = LoggerFactory.getLogger(UnsupportedMethodCommand.class);
    private final String operation;
    private final Resource<?, ?> resource;

    public UnsupportedMethodCommand(Resource<?, ?> resource, String operation) {
        super();
        Assert.notNull(operation, "operation is required");
        Assert.notNull(resource, "resource is required");
        this.operation = operation;
        this.resource = resource;
    }

    @Override
    public Object execute(Object... args) {
        log.warn("{} {} not supported", operation, resource);
        throw RequestError.builder()
                .codePrefix(operation)
                .codeSuffix("NOT_SUPPORTED")
                .resource(resource)
                .buildException();
    }

    public String getOperation() {
        return operation;
    }

    public Resource<?, ?> getResource() {
        return resource;
    }

}
