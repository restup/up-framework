package com.github.restup.controller.model;

public interface ResourceControllerResponse {

    void setStatus(int status);

    void setHeader(String name, String value);

}
