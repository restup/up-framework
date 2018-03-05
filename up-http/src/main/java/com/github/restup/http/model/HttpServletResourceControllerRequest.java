package com.github.restup.http.model;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import com.github.restup.controller.content.negotiation.ContentTypeNegotiation;
import com.github.restup.controller.model.AbstractResourceControllerRequestBuilder;
import com.github.restup.controller.model.BasicResourceControllerRequest;
import com.github.restup.controller.model.HttpMethod;
import com.github.restup.controller.model.MediaType;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRelationship;
import com.github.restup.service.model.ResourceData;

/**
 * Wraps {@link HttpServletRequest} to pass parameter details for Filtereds
 */
public class HttpServletResourceControllerRequest extends BasicResourceControllerRequest {

    private final HttpServletRequest request;

    protected HttpServletResourceControllerRequest(HttpServletRequest request, HttpMethod method, Resource<?, ?> resource, List<?> ids, Resource<?, ?> relationship, ResourceRelationship<?, ?, ?, ?> resourceRelationship, ResourceData<?> body, String baseRequestUrl, String requestUrl, String contentType) {
        super(method, resource, ids, relationship, resourceRelationship, body,
                contentType, baseRequestUrl, requestUrl);
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
        return request.getHeaders(name);
    }

    @Override
    public List<String> getParameterNames() {
        return Collections.list(request.getParameterNames());
    }

    @Override
    public String[] getParameter(String parameterName) {
        return request.getParameterValues(parameterName);
    }

    public static class Builder extends AbstractResourceControllerRequestBuilder<Builder, HttpServletResourceControllerRequest> {

        private final HttpServletRequest httpRequest;

        public Builder(HttpServletRequest httpRequest) {
            this.httpRequest = httpRequest;
        }
        
        @Override
        protected Builder me() {
            return super.me();
        }

        @Override
        public HttpServletResourceControllerRequest build() {
            String url = httpRequest.getRequestURL().toString();
            String path = httpRequest.getRequestURI();
            setBaseRequestUrl(url.substring(0, url.length() - path.length()));
            setRequestPath(path);
            setMethod(HttpMethod.of(httpRequest.getMethod()));
            parsePath();
            String contentType = getContentType(httpRequest);
            return new HttpServletResourceControllerRequest(httpRequest, method, resource, ids, relationship, resourceRelationship, body, baseRequestUrl, url, contentType);
        }
    }
}
