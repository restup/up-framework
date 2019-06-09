package com.github.restup.http.model;

import com.github.restup.controller.content.negotiation.ContentTypeNegotiation;
import com.github.restup.controller.model.AbstractResourceControllerRequestBuilder;
import com.github.restup.controller.model.BasicResourceControllerRequest;
import com.github.restup.controller.model.HttpMethod;
import com.github.restup.service.model.ResourceData;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 * Wraps {@link HttpServletRequest} to pass parameter details for Filtereds
 */
public class HttpServletResourceControllerRequest extends BasicResourceControllerRequest {

    private final HttpServletRequest request;

    protected HttpServletResourceControllerRequest(HttpServletRequest request, HttpMethod method,
        ResourceData<?> body, String baseRequestUrl, String requestUrl, String contentType) {
        super(method, body, contentType, baseRequestUrl, requestUrl);
        this.request = request;
    }

    public static String getContentType(HttpServletRequest request) {
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
    public Iterable<String> getHeaders(String name) {
        Enumeration<String> headers = request.getHeaders(name);
        return headers == null ? Collections.emptyList() : Collections.list(headers);
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
            baseRequestUrl(url.substring(0, url.length() - path.length()));
            requestPath(path);
            method(HttpMethod.of(httpRequest.getMethod()));
            String contentType = getContentType(
                () -> httpRequest.getParameterValues(contentTypeParam)
                , () -> HttpServletResourceControllerRequest.getContentType(httpRequest));
            return new HttpServletResourceControllerRequest(httpRequest, method, body,
                baseRequestUrl, url, contentType);
        }
    }
}
