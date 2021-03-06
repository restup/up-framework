package com.github.restup.service;

import com.github.restup.errors.RequestError;
import com.github.restup.registry.Resource;
import com.github.restup.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link MethodCommand} which always throws an {@link com.github.restup.errors.RequestErrorException} indicating that the operation is not supported.  Serves as a placeholder for services with missing operations
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
        log.warn("{} {} not supported", this.operation, this.resource);
        throw RequestError.builder()
            .codePrefix(this.operation)
                .codeSuffix("NOT_SUPPORTED")
            .resource(this.resource)
                .buildException();
    }

    public String getOperation() {
        return this.operation;
    }

    public Resource<?, ?> getResource() {
        return this.resource;
    }

}
