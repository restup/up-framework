package com.github.restup.test;

import java.util.Map;
import com.github.restup.test.resource.Contents;

public class BasicApiResponse<H> implements ApiResponse<H> {

    private final int status;
    private final Map<String, H> headers;
    private final Contents body;

    public BasicApiResponse(int status, Map<String, H> headers, Contents body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    protected BasicApiResponse(int status, Map<String, H> headers, byte[] body) {
        this(status, headers, Contents.of(body));
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public Map<String, H> getHeaders() {
        return headers;
    }

    @Override
    public Contents getBody() {
        return body;
    }

}
