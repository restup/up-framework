package com.github.restup.test;

public enum MediaType {
    APPLICATION_JSON("application/json"),
    APPLICATION_JSON_API("application/vnd.api+json"),
    APPLICATION_JSON_HAL("application/hal+json");

    private String contentType;

    MediaType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }
}