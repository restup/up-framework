package com.github.restup.controller.mock;

import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.restup.jackson.service.model.JacksonRequestBody;
import com.github.restup.service.model.ResourceData;
import com.github.restup.test.resource.Contents;

/**
 * Jackson implementation of {@link MockContentNegotiation}
 */
public class MockJacksonContentNegotiation implements MockContentNegotiation {

    private final ObjectMapper mapper;

    public MockJacksonContentNegotiation(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public ResourceData<?> getBody(Contents contents) {
        String body = contents == null ? null : contents.getContentAsString();
        if (body != null) {
            try {
                return mapper.readValue(body, JacksonRequestBody.class);
            } catch (IOException e) {
                throw new RuntimeException("unable to parse request");
            }
        }
        return null;
    }

    @Override
    public Contents serialize(Object result) throws Exception {
        String json = mapper.writeValueAsString(result);
        return Contents.of(json);
    }

}
