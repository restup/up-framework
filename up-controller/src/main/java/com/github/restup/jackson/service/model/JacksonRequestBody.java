package com.github.restup.jackson.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.restup.path.DataPathValue;
import com.github.restup.service.model.ResourceData;

/**
 * {@link ResourceData} implementation that deserializes a "data" field to a
 * JsonNode to be handled by {@link com.github.restup.jackson.parser.JacksonRequestBodyParser}
 */
public class JacksonRequestBody implements ResourceData<JsonNode> {

    @JsonProperty(DataPathValue.DATA)
    private JsonNode data;

    public JsonNode getData() {
        return data;
    }

    public void setData(JsonNode data) {
        this.data = data;
    }

}
