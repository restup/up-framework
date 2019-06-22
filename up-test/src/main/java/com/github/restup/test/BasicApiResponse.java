package com.github.restup.test;

import com.github.restup.test.resource.Contents;
import java.util.Map;

public class BasicApiResponse<H> implements ApiResponse<H> {

    private final int status;
    private final Map<String, H> headers;

    private final ApiResponseReader apiResponseReader;

    public BasicApiResponse(int status, Map<String, H> headers,
        ApiResponseReader apiResponseReader) {
        this.status = status;
        this.headers = headers;
        this.apiResponseReader = apiResponseReader;
    }

    public BasicApiResponse(int status, Map<String, H> headers, Contents body) {
        this(status, headers, new JsonPathApiResponseReader(body));
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
        return apiResponseReader.getBody();
    }

    @Override
    public <T> T read(String jsonPath) {
        return apiResponseReader.read(jsonPath);
    }

    @Override
    public <T> T read(String jsonPath, Class<T> type) {
        return apiResponseReader.read(jsonPath, type);
    }

    @Override
    public <T> T readId() {
        return apiResponseReader.readId();
    }

    public ApiResponseReader getApiResponseReader() {
        return apiResponseReader;
    }
}
