package com.github.restup.annotations.model;

public enum UpdateStrategy implements StatusCodeProvider {
    DEFAULT(StatusCode.OK),
    UPDATED(StatusCode.OK),
    ACCEPTED(StatusCode.ACCEPTED),
    NO_CONTENT(StatusCode.NO_CONTENT);

    private final StatusCode statusCode;

    UpdateStrategy(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public StatusCode getStatusCode() {
        return statusCode;
    }
}
