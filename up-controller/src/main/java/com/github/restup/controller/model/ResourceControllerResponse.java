package com.github.restup.controller.model;

import com.github.restup.annotations.model.StatusCode;

public interface ResourceControllerResponse {

    int getStatus();

    void setStatus(int status);

    default void setStatus(StatusCode status) {
        setStatus(status.getHttpStatus());
    }

    void setHeader(String name, String value);

}
