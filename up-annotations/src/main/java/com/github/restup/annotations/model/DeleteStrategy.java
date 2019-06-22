package com.github.restup.annotations.model;

public enum DeleteStrategy implements StatusCodeProvider {
    DEFAULT(StatusCode.NO_CONTENT),
    DELETED(StatusCode.OK),
    ACCEPTED(StatusCode.ACCEPTED),
    NO_CONTENT(StatusCode.NO_CONTENT),
    NOT_ALLOWED(StatusCode.METHOD_NOT_ALLOWED);

    private final StatusCode statusCode;

    DeleteStrategy(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public StatusCode getStatusCode() {
        return statusCode;
    }
}
