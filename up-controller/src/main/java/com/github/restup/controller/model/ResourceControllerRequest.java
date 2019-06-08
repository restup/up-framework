package com.github.restup.controller.model;

import com.github.restup.bind.param.ParameterProvider;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRelationship;
import com.github.restup.service.model.ResourceData;
import java.util.List;

/**
 * In an http request, this is a partially parsed details from the request, having parsed the request path to obtain resource info and ids.
 */
public interface ResourceControllerRequest extends ParameterProvider {

    Iterable<String> getHeaders(String name);

    String getContentType();

    HttpMethod getMethod();

    Resource<?, ?> getResource();

    Resource<?, ?> getRelationship();

    ResourceRelationship<?, ?, ?, ?> getResourceRelationship();

    List<?> getIds();

    ResourceData<?> getBody();

    String getBaseRequestUrl();

    String getRequestUrl();

}
