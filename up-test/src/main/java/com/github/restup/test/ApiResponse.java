package com.github.restup.test;

import com.github.restup.test.resource.ByteArrayContents;
import com.github.restup.test.resource.Contents;
import com.github.restup.test.resource.RelativeTestResource;
import org.hamcrest.Matcher;

import java.util.Map;

public class ApiResponse<H> {

    private final int status;
    private final Map<String, H> headers;
    private final Contents body;

    public ApiResponse(int status, Map<String, H> headers, Contents body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public ApiResponse(int status, Map<String, H> headers, byte[] body) {
        this(status, headers, new ByteArrayContents(body));
    }

    public static Builder builder() {
        return new Builder();
    }

    public H getHeader(String key) {
        for (Map.Entry<String, H> e : headers.entrySet()) {
            if (key.equalsIgnoreCase(e.getKey())) {
                return e.getValue();
            }
        }
        return null;
    }

    public int getStatus() {
        return status;
    }

    public Map<String, H> getHeaders() {
        return headers;
    }

    public Contents getBody() {
        return body;
    }

    public static class Builder extends ApiRequest.AbstractBuilder<Builder, Matcher<String[]>> {

        private int status;

        public Builder status(int status) {
            this.status = status;
            return me();
        }

        public Builder header(String name, Matcher<String[]> value) {
            headers.put(name, value);
            return me();
        }

        @Override
        protected String getTestDir() {
            return RelativeTestResource.RESPONSES;
        }

        public ApiResponse build() {
            return new ApiResponse(status, getHeaders(), getBody());
        }
    }
}
