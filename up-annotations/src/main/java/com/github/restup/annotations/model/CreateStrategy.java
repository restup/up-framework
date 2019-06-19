package com.github.restup.annotations.model;

public enum CreateStrategy implements StatusCodeProvider {
    DEFAULT(StatusCode.CREATED),
    CREATED(StatusCode.CREATED),
    ACCEPTED(StatusCode.ACCEPTED),
    NO_CONTENT(StatusCode.NO_CONTENT);

    private final StatusCode statusCode;

    CreateStrategy(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public StatusCode getStatusCode() {
        return statusCode;
    }
}
