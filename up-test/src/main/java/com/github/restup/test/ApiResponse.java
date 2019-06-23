package com.github.restup.test;

import com.github.restup.test.resource.RelativeTestResource;
import java.util.Map;
import org.hamcrest.Matcher;

public interface ApiResponse<H> extends ApiResponseReader {

    static Builder builder() {
        return new Builder();
    }


    default H getHeader(String key) {
        for (Map.Entry<String, H> e : getHeaders().entrySet()) {
            if (key.equalsIgnoreCase(e.getKey())) {
                return e.getValue();
            }
        }
        return null;
    }
    
    int getStatus();

    Map<String, H> getHeaders();

    /**
     * @return The Location header if present in the response, null otherwise. Typically available
     * for create resource
     */
    default String getLocation() {
        Object location = getHeader("location");
        String result = null;
        if (location instanceof String[]) {
            String[] arr = (String[]) location;
            if (arr.length > 0) {
                result = arr[0];
            }
        } else if (location instanceof String) {
            result = (String) location;
        }
        return result;
    }

    class Builder extends AbstractApiRequestBuilder<Builder, Matcher<String>> {

        private int status;

        public Builder status(int status) {
            this.status = status;
            return me();
        }

        @Override
        protected String getTestDir() {
            return RelativeTestResource.RESPONSES;
        }

        public ApiResponse<Matcher<String>> build() {
            return new BasicApiResponse<>(status, getHeaders(), getBody());
        }
    }
}
