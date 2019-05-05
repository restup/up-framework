package com.github.restup.errors;

public enum ErrorCode {
    BODY_REQUIRED("Body is required", "Body may not be empty"),
    BODY_ARRAY_NOT_SUPPORTED("Body array is not supported"),
    BODY_INVALID("Unable to deserialize body"),
    ID_NOT_ALLOWED_ON_CREATE("Id not allowed", "Id is auto generated and may not be specified"),
    UNKNOWN_RESOURCE("Unknown resource"),
    UNEXPECTED_FIND_RESULTS("Find returned more than one result"),
    SERIALIZATION_ERROR("Unable to serialize response"),
    INVALID_RELATIONSHIP("Invalid Relationship"),
    ID_REQUIRED("id is required", "Missing identifier at specified path"),
    TYPE_REQUIRED("type is required", "Missing type at specified path"),
    WRAP_FIELDS_WITH_ATTRIBUTES("field must be wrapped",
        "The field is valid, but it must be wrapped in attributes"),
    PARAMETER_INVALID("Invalid parameter value"),
    PARAMETER_INVALID_RESOURCE("Invalid parameter resource specified");

    private final String title;
    private final String detail;

    ErrorCode(String title, String detail) {
        this.title = title;
        this.detail = detail;
    }

    ErrorCode(String title) {
        this(title, title);
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }
}