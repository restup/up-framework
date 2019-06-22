package com.github.restup.test;

import com.github.restup.test.resource.Contents;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

public class JsonPathApiResponseReader implements ApiResponseReader {

    private final Contents body;
    private DocumentContext documentContext;

    public JsonPathApiResponseReader(Contents body) {
        this.body = body;
    }

    public synchronized DocumentContext getDocumentContext() {
        if (documentContext == null) {
            documentContext = JsonPath.parse(body == null ? "{}" : body.getContentAsString());
        }
        return documentContext;
    }

    @Override
    public Contents getBody() {
        return body;
    }

    @Override
    public <T> T read(String jsonPath) {
        return getDocumentContext().read(jsonPath);
    }

    @Override
    public <T> T read(String jsonPath, Class<T> c) {
        return getDocumentContext().read(jsonPath, c);
    }


}
