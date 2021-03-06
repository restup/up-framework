package com.github.restup.controller.request.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.settings.BuilderSettingsCaptor;
import com.github.restup.jackson.JacksonConfiguration;
import com.github.restup.registry.settings.AutoDetectConstants;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Parses {@link ResourceControllerRequest}
 */
public interface RequestParser {

    static Builder builder() {
        return new Builder();
    }

    /**
     * parse request appending results to builder
     *
     * @param request ResourceControllerRequest
     * @param builder ParsedResourceControllerRequest.Builder
     */
    void parse(ResourceControllerRequest request,
        ParsedResourceControllerRequest.Builder<?> builder);

    class Builder {

        private RequestParser[] requestParsers = {};
        private RequestParamParser.Builder requestParamParser;
        private RequestParser relationshipParser;
        private ObjectMapper mapper;
        private BuilderSettingsCaptor settingsCaptor;

        Builder() {
            super();
            settingsCaptor = new BuilderSettingsCaptor();
        }

        Builder me() {
            return this;
        }

        public Builder requestParsers(RequestParser... requestParsers) {
            this.requestParsers = requestParsers;
            return me();
        }

        public Builder relationshipParser(RequestParser relationshipParser) {
            this.relationshipParser = relationshipParser;
            return me();
        }

        public Builder requestParamParser(RequestParamParser.Builder builder) {
            requestParamParser = builder;
            return me();
        }

        public Builder jacksonObjectMapper(ObjectMapper mapper) {
            this.mapper = mapper;
            return me();
        }

        public Builder autoDetectDisabled(boolean autoDetectDisabled) {
            settingsCaptor.setAutoDetectDisabled(autoDetectDisabled);
            return me();
        }

        public Builder defaultMediaType(String mediaType) {
            settingsCaptor.setDefaultMediaType(mediaType);
            return me();
        }

        public Builder capture(BuilderSettingsCaptor settingsCaptor) {
            this.settingsCaptor = settingsCaptor.capture(this.settingsCaptor);
            return me();
        }

        public RequestParser build() {
            settingsCaptor.build();

            RequestParamParser.Builder b = requestParamParser;
            if (b == null) {
                b = RequestParamParser.builder().withDefaults();
            }
            ParameterParserChain parameterParserChain = b.build();

            if (!settingsCaptor.getAutoDetectDisabled()) {
                // Jackson as the default request parser if jackson exists
                if (AutoDetectConstants.JACKSON2_EXISTS) {
                    requestParsers(JacksonConfiguration
                        .parser(mapper, settingsCaptor.getDefaultMediaType()));
                }
                // add Gson other parsers by default?
            }

            RequestParser relationshipsParser = relationshipParser;
            if (relationshipsParser == null) {
                relationshipsParser = new DefaultRelationshipsParser();
            }

            if (ArrayUtils.getLength(requestParsers) == 0) {
                return new RequestParserChain(parameterParserChain, relationshipsParser);
            }
            return new RequestParserChain(
                ArrayUtils.addAll(requestParsers, parameterParserChain, relationshipsParser));
        }

    }
}
