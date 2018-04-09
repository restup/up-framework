package com.github.restup.test;

import com.github.restup.test.resource.Contents;
import java.util.Map;

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
        return this.method;
    }

    @Override
    public Contents getBody() {
        return this.body;
    }

    @Override
    public Map<String, String[]> getHeaders() {
        return this.headers;
    }

    @Override
    public boolean isHttps() {
        return this.https;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public String toString() {
        return "BasicApiRequest{" +
            "method=" + this.method +
            ", headers=" + this.headers +
            ", url='" + this.url + '\'' +
            ", body=" + this.body +
            ", https=" + this.https +
            '}';
    }
}
