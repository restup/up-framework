package com.github.restup.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.restup.controller.linking.LinksResult;
import com.github.restup.controller.model.MediaType;
import com.github.restup.controller.model.result.JsonApiResult;
import com.github.restup.controller.model.result.JsonResult;
import com.github.restup.controller.request.parser.ContentNegotiatedRequestParser;
import com.github.restup.controller.request.parser.RequestParser;
import com.github.restup.controller.request.parser.UnsupportedMediaTypeBodyRequestParser;
import com.github.restup.errors.RequestErrorException;
import com.github.restup.errors.RequestError;
import com.github.restup.jackson.mixins.BasicPagedResultMixin;
import com.github.restup.jackson.mixins.RequestErrorExceptionMixin;
import com.github.restup.jackson.mixins.JsonApiResultMixin;
import com.github.restup.jackson.mixins.JsonResultMixin;
import com.github.restup.jackson.mixins.LinksResultMixin;
import com.github.restup.jackson.mixins.RequestErrorMixin;
import com.github.restup.jackson.mixins.ResourceDataMixin;
import com.github.restup.jackson.mixins.ResourcePathMixin;
import com.github.restup.jackson.parser.JacksonJsonApiRequestBodyParser;
import com.github.restup.jackson.parser.JacksonRequestBodyParser;
import com.github.restup.path.ResourcePath;
import com.github.restup.service.model.ResourceData;
import com.github.restup.service.model.response.PagedResult;

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
        mapper.addMixIn(RequestErrorException.class, RequestErrorExceptionMixin.class);
        mapper.addMixIn(ResourcePath.class, ResourcePathMixin.class);
        mapper.addMixIn(LinksResult.class, LinksResultMixin.class);

        mapper.addMixIn(PagedResult.class, BasicPagedResultMixin.class);
        mapper.addMixIn(ResourceData.class, ResourceDataMixin.class);

        // XXX feature module may be "better"?
        mapper.addMixIn(JsonResult.class, JsonResultMixin.class);
        mapper.addMixIn(JsonApiResult.class, JsonApiResultMixin.class);
        return mapper;
    }

    public static RequestParser parser(ObjectMapper mapper, String defaultMediaType) {
        return ContentNegotiatedRequestParser.builder()
                .addParser(MediaType.APPLICATION_JSON, new JacksonRequestBodyParser(configure(mapper)))
                .addParser(MediaType.APPLICATION_JSON_API, new JacksonJsonApiRequestBodyParser(configure(mapper)))
                .defaultParser(new UnsupportedMediaTypeBodyRequestParser())
                .defaultMediaType(defaultMediaType)
                .build();
    }

    private JacksonConfiguration() {

    }

}
