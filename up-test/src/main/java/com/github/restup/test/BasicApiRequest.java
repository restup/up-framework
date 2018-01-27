package com.github.restup.test;

import java.util.Map;
import com.github.restup.test.resource.Contents;

class BasicApiRequest implements ApiRequest {

    private final HttpMethod method;
    private final Map<String, String[]> headers;
    private final String url;
    private final Contents body;
    private final boolean https;

    BasicApiRequest(HttpMethod method, Map<String, String[]> headers, String url, Contents body, boolean https) {
        this.method = method;
        this.headers = headers;
        this.url = url;
        this.body = body;
        this.https = https;
    }

    BasicApiRequest(HttpMethod method, Map<String, String[]> headers, String url, byte[] body, boolean https) {
        this(method, headers, url, Contents.of(body), https);
    }

    @Override
    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public Contents getBody() {
        return body;
    }

    @Override
    public Map<String, String[]> getHeaders() {
        return headers;
    }

    @Override
    public boolean isHttps() {
        return https;
    }

    @Override
    public String getUrl() {
        return url;
    }

}
