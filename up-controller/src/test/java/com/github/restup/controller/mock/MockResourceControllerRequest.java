package com.github.restup.controller.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.github.restup.controller.model.BasicResourceControllerRequest;
import com.github.restup.controller.model.HttpMethod;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRelationship;
import com.github.restup.service.model.ResourceData;
import com.github.restup.util.Assert;
import com.github.restup.util.UpUtils;

/**
 * Mock {@link ResourceControllerRequest} for testing
 */
public class MockResourceControllerRequest extends BasicResourceControllerRequest {

    private final static String LOCALHOST = "http://localhost";
    private final Map<String, String[]> parameters;

    protected MockResourceControllerRequest(Map<String, String[]> parameters
            , HttpMethod method
            , Resource<?, ?> resource
            , List<?> ids
            , Resource<?, ?> relationship
            , ResourceRelationship<?, ?, ?, ?> resourceRelationship
            , ResourceData<?> body
            , String contentType
            , String baseRequestUrl
            , String requestUrl) {
        super(method, resource, ids, relationship, resourceRelationship, body, contentType, baseRequestUrl, requestUrl);
        this.parameters = parameters;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static String getUrl(String path) {
        return LOCALHOST + (path.startsWith("/") ? "" : "/") + path;
    }

    @Override
    public List<String> getParameterNames() {
        List<String> result = new ArrayList<String>(parameters.keySet());
        Collections.sort(result);
        return result;
    }

    @Override
    public String[] getParameter(String parameterName) {
        return parameters.get(parameterName);
    }

    public static class Builder extends AbstractBuilder<Builder, MockResourceControllerRequest> {

        private HttpMethod method;
        private String url;
        private Map<String, String[]> headers = new HashMap<String, String[]>();

        public Builder method(HttpMethod method) {
            this.method = method;
            return me();
        }

        public Builder method(String method) {
            return method(HttpMethod.valueOf(method));
        }

        public Builder url(String url) {
            this.url = url;
            return me();
        }

        public Builder headers(Map<String, String[]> headers) {
            this.headers.putAll(headers);
            return me();
        }

        @Override
        public MockResourceControllerRequest build() {
            Assert.notNull(url, "URL must not be null");
            String[] parts = url.split("\\?");

            Map<String, String[]> parameters = parts.length > 1 ?
                    parseParams(parts[1])
                    : Collections.emptyMap();

            String path = parts[0];
            String url = getUrl(path);
            String contentType = headers.get("Content-Type")[0].split(";")[0];
            setBaseRequestUrl(LOCALHOST);
            setRequestPath(path);
            setMethod(method);
            parsePath();
            return new MockResourceControllerRequest(parameters, method, resource, ids, relationship, resourceRelationship, body, contentType, baseRequestUrl, url);
        }

        private Map<String, String[]> parseParams(String part) {
            Map<String, String[]> map = new HashMap<String, String[]>();
            if (part != null) {
                String[] pairs = part.split("&");
                for (String pair : pairs) {
                    String[] param = pair.split("=");
                    UpUtils.put(map, param[0], param[1]);
                }
            }
            return map;
        }
    }
}
