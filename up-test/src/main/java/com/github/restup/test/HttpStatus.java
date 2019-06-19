package com.github.restup.test;

public enum HttpStatus {

    OK(200),

    CREATED(201),

    ACCEPTED(202),

    NO_CONTENT(204),

    INTERNAL_SERVER_ERROR(500),

    BAD_REQUEST(400),

    FORBIDDEN(403),

    NOT_FOUND(404),

    CONFLICT(409),

    UNSUPPORTED_MEDIA_TYPE(415),

    METHOD_NOT_ALLOWED(405);


    final int httpStatus;

    HttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
