package com.github.restup.controller.request.parser;

import com.github.restup.controller.model.MediaType;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.util.Assert;

import java.util.HashMap;
import java.util.Map;

public class ContentNegotiatedRequestParser implements RequestParser {

    private final Map<String, RequestParser> parsers;
    private final RequestParser defaultParser;

    ContentNegotiatedRequestParser(Map<String, RequestParser> parsers, RequestParser defaultParser) {
        Assert.notEmpty(parsers, "parsers cannot be empty");
        Assert.notNull(defaultParser, "default parser is required");
        this.parsers = parsers;
        this.defaultParser = defaultParser;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void parse(ResourceControllerRequest request, ParsedResourceControllerRequest.Builder<?> builder) {
        RequestParser parser = parsers.get(request.getContentType());
        if (parser == null) {
            parser = defaultParser;
        }
        parser.parse(request, builder);
    }

    public final static class Builder {
        private Map<String, RequestParser> parsers = new HashMap<String, RequestParser>();
        private RequestParser defaultParser;

        private Builder() {

        }

        private Builder me() {
            return this;
        }

        public Builder addParser(MediaType contentType, RequestParser parser) {
            parsers.put(contentType.getContentType(), parser);
            return me();
        }

        public Builder defaultParser(RequestParser defaultParser) {
            this.defaultParser = defaultParser;
            return me();
        }

        public ContentNegotiatedRequestParser build() {
            RequestParser requestParser = defaultParser;
            if (requestParser == null) {
                requestParser = new NoOpRequestParser();
            }
            return new ContentNegotiatedRequestParser(parsers, requestParser);
        }
    }
}
