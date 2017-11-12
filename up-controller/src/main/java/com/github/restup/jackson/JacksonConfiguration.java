package com.github.restup.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.restup.controller.model.MediaType;
import com.github.restup.controller.model.result.JsonApiResult;
import com.github.restup.controller.model.result.JsonResult;
import com.github.restup.controller.request.parser.ContentNegotiatedRequestParser;
import com.github.restup.controller.request.parser.RequestParser;
import com.github.restup.controller.request.parser.UnsupportedMediaTypeBodeRequestParser;
import com.github.restup.errors.ErrorObjectException;
import com.github.restup.errors.RequestError;
import com.github.restup.jackson.mixins.*;
import com.github.restup.jackson.parser.JacksonJsonApiRequestBodyParser;
import com.github.restup.jackson.parser.JacksonRequestBodyParser;
import com.github.restup.path.ResourcePath;
import com.github.restup.service.model.ResourceData;
import com.github.restup.service.model.response.BasicPagedResult;

public class JacksonConfiguration {

    public static ObjectMapper configure() {
        return configure(null);
    }

    public static ObjectMapper configure(ObjectMapper target) {
        ObjectMapper mapper = target;
        if (mapper == null) {
            mapper = new ObjectMapper();
        }
        // errors
        mapper.addMixIn(RequestError.class, RequestErrorMixin.class);
        mapper.addMixIn(ErrorObjectException.class, ErrorObjectExceptionMixin.class);
        mapper.addMixIn(ResourcePath.class, ResourcePathMixin.class);

        mapper.addMixIn(BasicPagedResult.class, BasicPagedResultMixin.class);
        mapper.addMixIn(ResourceData.class, ResourceDataMixin.class);

        // XXX feature module may be "better"?
        mapper.addMixIn(JsonResult.class, JsonResultMixin.class);
        mapper.addMixIn(JsonApiResult.class, JsonApiResultMixin.class);
        return mapper;
    }

    public static RequestParser parser(ObjectMapper mapper) {
        return ContentNegotiatedRequestParser.builder()
                .addParser(MediaType.APPLICATION_JSON, new JacksonRequestBodyParser(configure(mapper)))
                .addParser(MediaType.APPLICATION_JSON_API, new JacksonJsonApiRequestBodyParser(configure(mapper)))
                .defaultParser(new UnsupportedMediaTypeBodeRequestParser())
                .build();
    }

}
