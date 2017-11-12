package com.github.restup.controller.mock;

import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.model.HttpMethod;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRelationship;
import com.github.restup.service.model.ResourceData;
import com.github.restup.util.Assert;
import com.github.restup.util.UpUtils;

import java.util.*;

/**
 * Mock {@link ResourceControllerRequest} for testing
 */
public class MockResourceControllerRequest extends ResourceControllerRequest {
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

    public List<String> getParameterNames() {
        List<String> result = new ArrayList<String>(parameters.keySet());
        Collections.sort(result);
        return result;
    }

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

        public MockResourceControllerRequest build() {
            Assert.notNull(url, "URL must not be null");
            String[] parts = url.split("\\?");

            Map<String, String[]> parameters = parts.length > 1 ?
                    parseParams(parts[1])
                    : (Map) Collections.emptyMap();

            String path = parts[0];
            String host = "http://localhost";
            String url = host+path;
            String contentType = headers.get("Content-Type")[0].split(";")[0];
            setBaseRequestUrl(host);
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
