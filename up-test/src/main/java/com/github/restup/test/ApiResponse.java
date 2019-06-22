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

    class Builder extends AbstractApiRequestBuilder<Builder, Matcher<String[]>> {

        private int status;

        public Builder status(int status) {
            this.status = status;
            return me();
        }

        @Override
        protected String getTestDir() {
            return RelativeTestResource.RESPONSES;
        }

        public ApiResponse<Matcher<String[]>> build() {
            return new BasicApiResponse<>(status, getHeaders(), getBody());
        }
    }
}
