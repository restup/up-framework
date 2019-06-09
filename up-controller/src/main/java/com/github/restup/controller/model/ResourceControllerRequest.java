package com.github.restup.controller.model;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import com.github.restup.bind.param.ParameterProvider;
import com.github.restup.service.model.ResourceData;

/**
 * In an http request, this is a partially parsed details from the request, having parsed the request path to obtain resource info and ids.
 */
public interface ResourceControllerRequest extends ParameterProvider {

    Iterable<String> getHeaders(String name);

    String getContentType();

    HttpMethod getMethod();

    ResourceData<?> getBody();

    String getBaseRequestUrl();

    String getRequestUrl();

    default String getRequestPath() {
        String requestUrl = getRequestUrl();
        String baseRequestUrl = getBaseRequestUrl();
        if (isNotEmpty(baseRequestUrl)) {
            requestUrl = requestUrl.replace(baseRequestUrl, "");
        }
        return requestUrl;
    }
}
