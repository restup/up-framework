package com.github.restup.controller.request.parser;

import com.github.restup.controller.model.MediaType;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.request.parser.path.RequestPathParserResult;
import com.github.restup.util.Assert;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class ContentNegotiatedRequestParser implements RequestParser {

    private final Map<String, RequestParser> parsers;
    private final RequestParser defaultParser;
    private final String defaultMediaType;

    ContentNegotiatedRequestParser(Map<String, RequestParser> parsers, RequestParser defaultParser, String defaultMediaType) {
        Assert.notEmpty(parsers, "parsers cannot be empty");
        Assert.notNull(defaultParser, "default parser is required");
        this.parsers = parsers;
        this.defaultParser = defaultParser;
        this.defaultMediaType = defaultMediaType;
    }

    public static Builder builder() {
        return new Builder();
    }

    private String getContentType(ResourceControllerRequest request) {
        String result = request.getContentType();
        if (StringUtils.isEmpty(result)) {
            result = defaultMediaType;
        }
        return result;
    }

    @Override
    public void parse(ResourceControllerRequest request,
        RequestPathParserResult requestPathParserResult,
        ParsedResourceControllerRequest.Builder<?> builder) {
        RequestParser parser = parsers.get(getContentType(request));
        if (parser == null) {
            parser = defaultParser;
        }

        parser.parse(request, requestPathParserResult, builder);
    }

    public final static class Builder {

        private Map<String, RequestParser> parsers = new HashMap<>();
        private RequestParser defaultParser;
        private String defaultMediaType;

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

        public Builder defaultMediaType(String defaultMediaType) {
            this.defaultMediaType = defaultMediaType;
            return me();
        }

        public ContentNegotiatedRequestParser build() {
            RequestParser requestParser = defaultParser;
            if (requestParser == null) {
                requestParser = new NoOpRequestParser();
            }
            return new ContentNegotiatedRequestParser(parsers, requestParser, defaultMediaType);
        }
    }
}
