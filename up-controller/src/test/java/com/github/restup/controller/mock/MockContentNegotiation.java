package com.github.restup.controller.mock;

import com.github.restup.service.model.ResourceData;
import com.github.restup.test.resource.Contents;

/**
 * Mocked content negotiation to allow {@link MockApiExecutor} to be used with different serialization libraries (Jackson, Gson, etc)
 */
public interface MockContentNegotiation {

    /**
     * Parse the Contents to ResourceData.
     */
    ResourceData<?> getBody(Contents contents);

    /**
     * serialize the result
     */
    Contents serialize(Object result) throws Exception;
}
