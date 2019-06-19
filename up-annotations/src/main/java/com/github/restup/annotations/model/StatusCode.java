package com.github.restup.annotations.model;

public enum StatusCode {
    OK(200),
    CREATED(201),
    ACCEPTED(202),
    NO_CONTENT(204),
    INTERNAL_SERVER_ERROR(500, "Unexpected Error", "The server encountered an unexpected condition which prevented it from fulfilling the request."),
    BAD_REQUEST(400, "Bad Request", "The request could not be understood by the server due to malformed syntax"),
    FORBIDDEN(403, "Forbidden", "The request is forbidden˙"),
    NOT_FOUND(404, "Not Found", "The requested resource does not exist"),
    CONFLICT(409, "Conflict", "The specified identifier already exists"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type", "Media type is not supported"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed", "The method specified in the Request-Line is not allowed for the resource identified by the Request-URI.");

    final private int httpStatus;
    final private String defaultTitle;
    final private String defaultDetail;

    StatusCode(int httpStatus, String defaultTitle, String defaultDetail) {
        this.httpStatus = httpStatus;
        this.defaultTitle = defaultTitle;
        this.defaultDetail = defaultDetail;
    }

    StatusCode(int httpStatus) {
        this(httpStatus, null, null);
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getDefaultTitle() {
        return defaultTitle;
    }

    public String getDefaultDetail() {
        return defaultDetail;
    }
}