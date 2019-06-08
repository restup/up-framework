package com.github.restup.test;

import com.github.restup.test.resource.Contents;
import java.util.Map;

class BasicApiRequest implements ApiRequest {

    private final HttpMethod method;
    private final Map<String, String[]> headers;
    private final Map<String, String[]> params;
    private final String path;
    private final String url;
    private final Contents body;
    private final boolean https;

    BasicApiRequest(HttpMethod method, Map<String, String[]> headers, String url, Contents body,
        boolean https, String path, Map<String, String[]> params) {
        this.method = method;
        this.headers = headers;
        this.params = params;
        this.url = url;
        this.path = path;
        this.body = body;
        this.https = https;
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

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Map<String, String[]> getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "BasicApiRequest{" +
            "method=" + method +
            ", headers=" + headers +
            ", url='" + url + '\'' +
            ", body=" + body +
            ", https=" + https +
            '}';
    }
}
