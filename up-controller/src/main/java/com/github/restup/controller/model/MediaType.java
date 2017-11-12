package com.github.restup.controller.model;

public enum MediaType {

    APPLICATION_JSON("application/json"),
    APPLICATION_JSON_API("application/vnd.api+json"),
    APPLICATION_JSON_HAL("application/hal+json");

    public static final String PARAM = "mediaType";

    private String contentType;

    MediaType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }

}
