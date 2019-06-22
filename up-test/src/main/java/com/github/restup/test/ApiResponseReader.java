package com.github.restup.test;

import com.github.restup.test.resource.Contents;

public interface ApiResponseReader {


    Contents getBody();

    <T> T read(String jsonPath);

    <T> T read(String jsonPath, Class<T> type);

    default <T> T readId() {
        return read("data.id");
    }

}
