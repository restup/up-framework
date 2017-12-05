package com.github.restup.test.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonSerializer implements SerializationProvider {

    private static ObjectMapper mapper;

    private static ObjectMapper getMapper() {
        if (mapper == null) {
            mapper = new ObjectMapper();
        }
        return mapper;
    }

    public static String convertToString(Object o) {
        return convertToString(null, o);
    }

    public static String convertToString(ObjectMapper mapper, Object o) {
        try {
            if (mapper == null) {
                mapper = getMapper();
            }
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to serialize value", e);
        }
    }

}
