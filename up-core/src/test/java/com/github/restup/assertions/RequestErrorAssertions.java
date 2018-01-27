package com.github.restup.assertions;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import java.util.Map;
import com.github.restup.errors.RequestError;

public class RequestErrorAssertions {
    
    private RequestError error;
    
    
    RequestErrorAssertions(RequestError error) {
        this.error = error;
    }
    
    private RequestErrorAssertions me() {
        return this;
    }

    public RequestErrorAssertions id(String id) {
        assertThat(error.getId(), is(id));
        return me();
    }

    public RequestErrorAssertions code(String code) {
        assertThat(error.getCode(), is(code));
        return me();
    }

    public RequestErrorAssertions title(String title) {
        assertThat(error.getTitle(), is(title));
        return me();
    }

    public RequestErrorAssertions detail(String detail) {
        assertThat(error.getDetail(), is(detail));
        return me();
    }

    public RequestErrorAssertions status(String status) {
        assertThat(error.getStatus(), is(status));
        return me();
    }

    public RequestErrorAssertions httpStatus(int status) {
        assertThat(error.getHttpStatus(), is(status));
        return me();
    }

    public RequestErrorAssertions meta(Object meta) {
        assertThat(error.getMeta(), is(meta));
        return me();
    }

    @SuppressWarnings("unchecked")
    public RequestErrorAssertions meta(String key, Object value) {
        assertThat(error.getMeta(), instanceOf(Map.class));
        Map<String,Object> meta = (Map<String,Object>)error.getMeta();
        assertThat(meta.get(key), is(value));
        return me();
    }
    
}
