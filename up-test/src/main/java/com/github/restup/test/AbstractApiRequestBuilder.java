package com.github.restup.test;

import com.github.restup.test.resource.Contents;
import com.github.restup.test.resource.RelativeTestResource;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractApiRequestBuilder<B extends AbstractApiRequestBuilder<B, H>, H> {

    protected Map<String, H> headers = new HashMap<>();
    private Class<?> testClass;
    private String testName;
    private String testFileExtension;
    private Contents bodyContents;

    protected B me() {
        return (B) this;
    }

    public B body(byte[] body) {
        return body(Contents.of(body));
    }

    public B body(String body) {
        return body(Contents.of(body));
    }

    public B body(Contents bodyContents) {
        this.bodyContents = bodyContents;
        return me();
    }

    public B header(String name, H value) {
        headers.put(name, value);
        return me();
    }

    public B headers(Map<String, H> headers) {
        this.headers = headers;
        return me();
    }

    public B testClass(Class<?> testClass) {
        this.testClass = testClass;
        return me();
    }

    public B testName(String testName) {
        this.testName = testName;
        return me();
    }

    public B testFileExtension(String testFileExtension) {
        this.testFileExtension = testFileExtension;
        return me();
    }

    protected void add(Map<String, String[]> map, String key, String... values) {
        if (StringUtils.isNotEmpty(key)) {
            String[] existing = map.get(key);
            if (existing == null) {
                map.put(key, values);
            } else {
                map.put(key, ArrayUtils.addAll(existing, values));
            }
        }
    }

    protected Map<String, H> getHeaders() {
        return headers;
    }

    protected abstract String getTestDir();

    protected boolean isDefaultTestResourceAllowed() {
        return StringUtils.isNotBlank(testName);
    }

    protected boolean hasConfiguredBody() {
        return bodyContents != null || isDefaultTestResourceAllowed();
    }

    protected Contents getContents() {
        return bodyContents;
    }

    public Contents getBody() {
        if (bodyContents != null) {
            return bodyContents;
        }
        if (isDefaultTestResourceAllowed()) {
            RelativeTestResource resource = new RelativeTestResource(testClass, getTestDir(), testName, testFileExtension);
            return resource;
        }
        return null;
    }

}