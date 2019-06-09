package com.github.restup.controller.mock;

import com.github.restup.controller.model.AbstractResourceControllerRequestBuilder;
import com.github.restup.controller.model.BasicResourceControllerRequest;
import com.github.restup.controller.model.HttpMethod;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.service.model.ResourceData;
import com.github.restup.util.Assert;
import com.github.restup.util.UpUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mock {@link ResourceControllerRequest} for testing
 */
public class MockResourceControllerRequest extends BasicResourceControllerRequest {

    private final static String LOCALHOST = "http://localhost";
    private final Map<String, String[]> parameters;

    protected MockResourceControllerRequest(Map<String, String[]> parameters
            , HttpMethod method
            , ResourceData<?> body
            , String contentType
            , String baseRequestUrl
            , String requestUrl) {
        super(method, body, contentType, baseRequestUrl, requestUrl);
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
        List<String> result = new ArrayList<>(parameters.keySet());
        Collections.sort(result);
        return result;
    }

    @Override
    public String[] getParameter(String parameterName) {
        return parameters.get(parameterName);
    }

    public static class Builder extends AbstractResourceControllerRequestBuilder<Builder, MockResourceControllerRequest> {

        private String url;
        private Map<String, String[]> headers = new HashMap<>();
        private MockResourceControllerRequest result;

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

        private String getContentTypeHeader() {
            String[] contentType = headers.get("Content-Type");
            if (contentType != null && contentType.length > 0) {
                return contentType[0].split(";")[0];
            }
            return null;
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
            String contentType = getContentType(
                () -> parameters.get(contentTypeParam),
                this::getContentTypeHeader);

            baseRequestUrl(LOCALHOST)
                .requestPath(path)
                .method(method);

            result = new MockResourceControllerRequest(parameters, method, body, contentType,
                baseRequestUrl, url);
            return result;
        }

        MockResourceControllerRequest getResult() {
            return result;
        }

        private Map<String, String[]> parseParams(String part) {
            Map<String, String[]> map = new HashMap<>();
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
