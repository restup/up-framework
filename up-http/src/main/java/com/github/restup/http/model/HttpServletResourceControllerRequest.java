package com.github.restup.http.model;

import com.github.restup.controller.content.negotiation.ContentTypeNegotiation;
import com.github.restup.controller.model.HttpMethod;
import com.github.restup.controller.model.MediaType;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRelationship;
import com.github.restup.service.model.ResourceData;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Wraps {@link HttpServletRequest} to pass parameter details for Filtereds
 */
public class HttpServletResourceControllerRequest extends ResourceControllerRequest {

    private final HttpServletRequest request;

    protected HttpServletResourceControllerRequest(HttpServletRequest request, HttpMethod method, Resource<?, ?> resource, List<?> ids, Resource<?, ?> relationship, ResourceRelationship<?, ?, ?, ?> resourceRelationship, ResourceData<?> body, String baseRequestUrl, String requestUrl) {
        super(method, resource, ids, relationship, resourceRelationship, body,
                getContentType(request), baseRequestUrl, requestUrl);
        HttpMethod.of(request.getMethod());
        this.request = request;
    }

    public static String getContentType(HttpServletRequest request) {
        String param = request.getParameter(MediaType.PARAM);
        if (StringUtils.isNotEmpty(param)) {
            return param;
        }
        Enumeration<String> e = request.getHeaders(ContentTypeNegotiation.CONTENT_TYPE);
        if (e != null) {
            while (e.hasMoreElements()) {
                String type = e.nextElement();
                String[] arr = type.split(";");
                return arr[0].trim();
            }
        }
        return null;
    }

    public static Builder builder(HttpServletRequest httpRequest) {
        return new Builder(httpRequest);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return request == null ? null : request.getHeaders(name);
    }

    public List<String> getParameterNames() {
        return Collections.list(request.getParameterNames());
    }

    public String[] getParameter(String parameterName) {
        return request.getParameterValues(parameterName);
    }

    public static class Builder extends AbstractBuilder<Builder, HttpServletResourceControllerRequest> {
        private final HttpServletRequest httpRequest;

        public Builder(HttpServletRequest httpRequest) {
            this.httpRequest = httpRequest;
        }

        public HttpServletResourceControllerRequest build() {
            String url = httpRequest.getRequestURL().toString();
            String path = httpRequest.getRequestURI();
            setBaseRequestUrl(url.substring(0, url.length() - path.length()));
            setRequestPath(path);
            setMethod(HttpMethod.of(httpRequest.getMethod()));
            parsePath();
            return new HttpServletResourceControllerRequest(httpRequest, method, resource, ids, relationship, resourceRelationship, body, baseRequestUrl, url);
        }
    }
}
