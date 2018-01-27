package com.github.restup.http.model;

import com.github.restup.controller.model.ResourceControllerResponse;
import javax.servlet.http.HttpServletResponse;

public class HttpServletResourceControllerResponse implements ResourceControllerResponse {

    private final HttpServletResponse response;

    public HttpServletResourceControllerResponse(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public void setStatus(int status) {
        response.setStatus(status);
    }

    @Override
    public void setHeader(String name, String value) {
        response.setHeader(name, value);
    }

}
