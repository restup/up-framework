package com.github.restup.test.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonSerializer implements ResultSerializer {

    private static ObjectMapper instance;
    private ObjectMapper mapper;

    public JacksonSerializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }
    
    public JacksonSerializer() {
        this(getMapper());
    }

    private static ObjectMapper getMapper() {
        if (instance == null) {
            instance = new ObjectMapper();
        }
        return instance;
    }
    
    @Override
    public String convertToString(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof String) {
            return (String) o;
        }
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new AssertionError("Unable to serialize value", e);
        }
    }

}
