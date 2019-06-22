package com.github.restup.annotations.model;

public enum CreateStrategy implements StatusCodeProvider {
    DEFAULT(StatusCode.CREATED),
    CREATED(StatusCode.CREATED),
    ACCEPTED(StatusCode.ACCEPTED),
    NO_CONTENT(StatusCode.NO_CONTENT),
    NOT_ALLOWED(StatusCode.METHOD_NOT_ALLOWED);

    private final StatusCode statusCode;

    CreateStrategy(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public StatusCode getStatusCode() {
        return statusCode;
    }
}
