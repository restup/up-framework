package com.github.restup.controller.request.parser;

import static com.github.restup.util.Streams.forEachNonNull;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
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

/**
 * Parses {@link ResourceControllerRequest}
 */
public interface RequestParser {

    /**
     * parse request appending results to builder
     * 
     * @param request
     * @param builder
     */
    void parse(ResourceControllerRequest request, ParsedResourceControllerRequest.Builder<?> builder);

    static Builder builder() {
        return new Builder();
    }

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
            settingsCaptor = new BuilderSettingsCaptor();
        }

        Builder me() {
            return this;
        }

        public Builder pageOffsetParamName(String... names) {
            this.pageOffsetParamName = names;
            return me();
        }

        public Builder pageLimitParamName(String... names) {
            this.pageLimitParamName = names;
            return me();
        }

        public Builder sortParamName(String... names) {
            this.sortParamName = names;
            return me();
        }

        public Builder filterParamName(String... names) {
            this.filterParamName = names;
            return me();
        }

        public Builder includeParamName(String... names) {
            this.includeParamName = names;
            return me();
        }

        public Builder fieldsParamName(String... names) {
            this.fieldsParamName = names;
            return me();
        }

        public Builder pageNumberParamName(String... names) {
            this.pageNumberParamName = names;
            return me();
        }

        public Builder requestParsers(RequestParser... requestParsers) {
            this.requestParsers = requestParsers;
            return me();
        }

        public Builder relationshipParser(RequestParser relationshipParser) {
            this.relationshipParser = relationshipParser;
            return me();
        }

        public Builder requestParamParsers(RequestParamParser... requestParamParsers) {
            this.requestParamParsers = requestParamParsers;
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

            List<RequestParamParser> paramParsers = new ArrayList<>();
            forEachNonNull(requestParamParsers, p -> paramParsers.add(p));
            forEachNonNull(pageOffsetParamName, s -> paramParsers.add(new PageOffsetParser(s)));
            forEachNonNull(pageLimitParamName, s -> paramParsers.add(new PageLimitParser(s)));
            forEachNonNull(sortParamName, s -> paramParsers.add(new SortParamParser(s)));
            forEachNonNull(filterParamName, s -> paramParsers.add(new FilterParser(s)));
            forEachNonNull(includeParamName, s -> paramParsers.add(new IncludeParser(s)));
            forEachNonNull(fieldsParamName, s -> paramParsers.add(new FieldsParser(s)));
            forEachNonNull(pageNumberParamName, s -> paramParsers.add(new PageNumberParser(s)));

            ParameterParserChain parameterParserChain = ParameterParserChain.of(paramParsers);

            if (!settingsCaptor.getAutoDetectDisabled()) {
                if (AutoDetectConstants.JACKSON2_EXISTS) {
                    requestParsers(JacksonConfiguration.parser(mapper, settingsCaptor.getDefaultMediaType()));
                }
            }

            RequestParser relationshipsParser = this.relationshipParser;
            if (relationshipsParser == null) {
                relationshipsParser = new DefaultRelationshipsParser();
            }

            if (ArrayUtils.getLength(requestParsers) == 0) {
                return new RequestParserChain(parameterParserChain, relationshipsParser);
            }
            return new RequestParserChain(ArrayUtils.addAll(requestParsers, parameterParserChain, relationshipsParser));
        }

    }
}
