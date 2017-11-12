package com.github.restup.jackson.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.restup.controller.model.HttpMethod;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.errors.ErrorBuilder;
import com.github.restup.path.ResourcePath;
import com.github.restup.registry.Resource;

import java.util.Iterator;
import static  com.github.restup.controller.model.result.JsonApiResult.*;

/**
 * Jackson implementation for parsing request body per JSON API spec, taking into account id, type, attributes
 * structure and validating any spec violations.
 */
public class JacksonJsonApiRequestBodyParser extends JacksonRequestBodyParser {

    public JacksonJsonApiRequestBodyParser(ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    protected void graphObject(ResourceControllerRequest request, ParsedResourceControllerRequest.Builder<?> builder, Resource<?, ?> resource, ResourcePath parent, JsonNode node) {

        JsonNode id = null;
        JsonNode type = null;
        //TODO validations per spec
        JsonNode attributes = null;

        Iterator<String> it = node.fieldNames();
        while (it.hasNext()) {
            String fieldName = it.next();
            if (fieldName.equals(ID)) {
                id = node.get(ID);
            } else if (fieldName.equals(TYPE)) {
                type = node.get(TYPE);
            } else if (fieldName.equals(ATTRIBUTES)) {
                attributes = node.get(ATTRIBUTES);
            } else {
                ResourcePath.Builder path = path(parent, builder, fieldName);
                if (!path.isInvalid()) {
                    // if path is invalid, we don't have to add another path error, but if it is
                    // invalid and not an expected field (id, type, attributes), indicate to push field
                    // into attributes.

                    builder.addError(ErrorBuilder.builder(path.build())
                            .code(ErrorBuilder.ErrorCode.WRAP_FIELDS_WITH_ATTRIBUTES));
                }
            }
        }

        if (id == null) {
            if (!HttpMethod.POST.equals(request.getMethod())) {
                builder.addError(ErrorBuilder.builder(parent)
                        .code(ErrorBuilder.ErrorCode.ID_REQUIRED));
            }
            // TODO else required client generated ids?
        }
        if (type == null) {
            builder.addError(ErrorBuilder.builder(parent)
                    .code(ErrorBuilder.ErrorCode.TYPE_REQUIRED));
        }

        super.graphObject(request, builder, resource, parent, attributes);

    }

    @Override
    protected Object deserializeObject(ResourceControllerRequest details, ParsedResourceControllerRequest.Builder<?> builder, JsonNode node) {
        try {
            Resource<?, ?> resource = details.getResource();
            ObjectNode attributes = (ObjectNode) node.get(ATTRIBUTES);
            attributes.set(ID, node.get(ID));
            if ( resource.hasApiField(TYPE)) {
                attributes.set(TYPE, node.get(TYPE));
            }
            return mapper.treeToValue(attributes, resource.getType());
        } catch (JsonProcessingException e) {
            builder.addError(ErrorBuilder.ErrorCode.BODY_INVALID);
            return null;
        }
    }
}
