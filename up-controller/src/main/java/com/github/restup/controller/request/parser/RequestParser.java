package com.github.restup.controller.request.parser;

import static com.github.restup.util.Streams.forEachNonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.request.parser.params.FieldsParser;
import com.github.restup.controller.request.parser.params.FilterParser;
import com.github.restup.controller.request.parser.params.IncludeParser;
import com.github.restup.controller.request.parser.params.PageLimitParser;
import com.github.restup.controller.request.parser.params.PageNumberParser;
import com.github.restup.controller.request.parser.params.PageOffsetParser;
import com.github.restup.controller.request.parser.params.SortParamParser;
import com.github.restup.controller.settings.BuilderSettingsCaptor;
import com.github.restup.jackson.JacksonConfiguration;
import com.github.restup.registry.settings.AutoDetectConstants;
import java.util.ArrayList;
import java.util.List;
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
     * @param request to parse
     * @param builder handling request
     */
    void parse(ResourceControllerRequest request,
        ParsedResourceControllerRequest.Builder<?> builder);

    static class Builder {

        private RequestParser[] requestParsers = {};
        private RequestParamParser[] requestParamParsers = {};
        private RequestParser relationshipParser;
        private String[] pageOffsetParamName = {"offset"};
        private String[] pageLimitParamName = {"limit", "pageSize", "rpp"};
        private String[] sortParamName = {"sort"};
        private String[] filterParamName = {"filter", "f", "q"};
        private String[] includeParamName = {"include"};
        private String[] fieldsParamName = {"fields"};
        private String[] pageNumberParamName = {"pageNumber", "page", "pageNo", "pageNum"};
        private ObjectMapper mapper;
        private BuilderSettingsCaptor settingsCaptor;

        Builder() {
            super();
            this.settingsCaptor = new BuilderSettingsCaptor();
        }

        Builder me() {
            return this;
        }

        public Builder pageOffsetParamName(String... names) {
            this.pageOffsetParamName = names;
            return this.me();
        }

        public Builder pageLimitParamName(String... names) {
            this.pageLimitParamName = names;
            return this.me();
        }

        public Builder sortParamName(String... names) {
            this.sortParamName = names;
            return this.me();
        }

        public Builder filterParamName(String... names) {
            this.filterParamName = names;
            return this.me();
        }

        public Builder includeParamName(String... names) {
            this.includeParamName = names;
            return this.me();
        }

        public Builder fieldsParamName(String... names) {
            this.fieldsParamName = names;
            return this.me();
        }

        public Builder pageNumberParamName(String... names) {
            this.pageNumberParamName = names;
            return this.me();
        }

        public Builder requestParsers(RequestParser... requestParsers) {
            this.requestParsers = requestParsers;
            return this.me();
        }

        public Builder relationshipParser(RequestParser relationshipParser) {
            this.relationshipParser = relationshipParser;
            return this.me();
        }

        public Builder requestParamParsers(RequestParamParser... requestParamParsers) {
            this.requestParamParsers = requestParamParsers;
            return this.me();
        }

        public Builder jacksonObjectMapper(ObjectMapper mapper) {
            this.mapper = mapper;
            return this.me();
        }


        public Builder autoDetectDisabled(boolean autoDetectDisabled) {
            this.settingsCaptor.setAutoDetectDisabled(autoDetectDisabled);
            return this.me();
        }

        public Builder defaultMediaType(String mediaType) {
            this.settingsCaptor.setDefaultMediaType(mediaType);
            return this.me();
        }

        public Builder capture(BuilderSettingsCaptor settingsCaptor) {
            this.settingsCaptor = settingsCaptor.capture(this.settingsCaptor);
            return this.me();
        }

        public RequestParser build() {
            this.settingsCaptor.build();

            List<RequestParamParser> paramParsers = new ArrayList<>();
            forEachNonNull(this.requestParamParsers, p -> paramParsers.add(p));
            forEachNonNull(this.pageOffsetParamName,
                s -> paramParsers.add(new PageOffsetParser(s)));
            forEachNonNull(this.pageLimitParamName, s -> paramParsers.add(new PageLimitParser(s)));
            forEachNonNull(this.sortParamName, s -> paramParsers.add(new SortParamParser(s)));
            forEachNonNull(this.filterParamName, s -> paramParsers.add(new FilterParser(s)));
            forEachNonNull(this.includeParamName, s -> paramParsers.add(new IncludeParser(s)));
            forEachNonNull(this.fieldsParamName, s -> paramParsers.add(new FieldsParser(s)));
            forEachNonNull(this.pageNumberParamName,
                s -> paramParsers.add(new PageNumberParser(s)));

            ParameterParserChain parameterParserChain = ParameterParserChain.of(paramParsers);

            if (!this.settingsCaptor.getAutoDetectDisabled()) {
                if (AutoDetectConstants.JACKSON2_EXISTS) {
                    this.requestParsers(JacksonConfiguration
                        .parser(this.mapper, this.settingsCaptor.getDefaultMediaType()));
                }
            }

            RequestParser relationshipsParser = this.relationshipParser;
            if (relationshipsParser == null) {
                relationshipsParser = new DefaultRelationshipsParser();
            }

            if (ArrayUtils.getLength(this.requestParsers) == 0) {
                return new RequestParserChain(parameterParserChain, relationshipsParser);
            }
            return new RequestParserChain(
                ArrayUtils.addAll(this.requestParsers, parameterParserChain, relationshipsParser));
        }

    }
}
