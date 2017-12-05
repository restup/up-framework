package com.github.restup.controller.mock;

import com.github.restup.controller.model.ResourceControllerResponse;
import com.github.restup.util.UpUtils;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock {@link ResourceControllerResponse} for testing
 */
public class MockResourceControllerResponse implements ResourceControllerResponse {

    private int status;
    private Map<String, String[]> headers = new HashMap<String, String[]>();

    public void setHeader(String name, String value) {
        UpUtils.put(headers, name, value);
    }

    public Map<String, String[]> getHeaders() {
        return headers;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
