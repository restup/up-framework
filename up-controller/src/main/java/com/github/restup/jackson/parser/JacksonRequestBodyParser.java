package com.github.restup.jackson.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.request.parser.AbstractRequestBodyParser;
import com.github.restup.errors.ErrorCode;
import com.github.restup.path.ResourcePath;
import com.github.restup.registry.Resource;

public class JacksonRequestBodyParser extends AbstractRequestBodyParser<JsonNode> {

    protected final ObjectMapper mapper;

    public JacksonRequestBodyParser(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public JacksonRequestBodyParser() {
        this(new ObjectMapper());
    }

    @Override
    protected boolean isArray(JsonNode jsonNode) {
        return jsonNode.isArray();
    }

    @Override
    protected boolean isObject(JsonNode jsonNode) {
        return jsonNode.isObject();
    }

    @Override
    protected void graphArray(ResourceControllerRequest request, ParsedResourceControllerRequest.Builder<?> builder, Resource<?, ?> resource, ResourcePath parent, JsonNode node) {
        Iterator<JsonNode> it = node.iterator();
        int i = 0;
        while (it.hasNext()) {
            JsonNode item = it.next();
            ResourcePath path = path(parent).index(i++).build();
            graph(request, builder, resource, path, item);
        }
    }

    @Override
    protected void graphObject(ResourceControllerRequest request, ParsedResourceControllerRequest.Builder<?> builder, Resource<?, ?> resource, ResourcePath parent, JsonNode node) {

        Iterator<String> iterator = node.fieldNames();

        while (iterator.hasNext()) {
            String fieldName = iterator.next();
            ResourcePath path = path(parent, builder, fieldName).build();
            JsonNode item = node.get(fieldName);
            //TODO if is polymorphic type, type identifier is required.
            //TODO if patch lookup existing type identifier

            graph(request, builder, resource, path, item);
        }
    }

    @Override
    protected Object deserializeObject(ResourceControllerRequest details, ParsedResourceControllerRequest.Builder<?> builder, JsonNode node) {
        try {
            return mapper.treeToValue(node, details.getResource().getClassType());
        } catch (JsonProcessingException e) {
            builder.addError(ErrorCode.BODY_INVALID);
            return null;
        }
    }

    @Override
    protected Object deserializeArray(ResourceControllerRequest details, ParsedResourceControllerRequest.Builder<?> builder, JsonNode node) {
        List<Object> result = new ArrayList<Object>();
        Iterator<JsonNode> it = node.iterator();
        while (it.hasNext()) {
            JsonNode item = it.next();
            result.add(deserializeObject(details, builder, item));
        }
        return result;
    }
}
