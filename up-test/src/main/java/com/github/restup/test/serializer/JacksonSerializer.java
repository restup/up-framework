package com.github.restup.test.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonSerializer implements ResultSerializer {

    private static ObjectMapper instance;
    private ObjectMapper mapper;

    private static ObjectMapper getMapper() {
        if (instance == null) {
            instance = new ObjectMapper();
        }
        return instance;
    }
    
    public JacksonSerializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }
    
    public JacksonSerializer() {
        this(getMapper());
    }
    
    @Override
    public String convertToString(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new AssertionError("Unable to serialize value", e);
        }
    }

}
