package com.github.restup.controller.request.parser;

import static com.github.restup.controller.request.parser.params.ComposedRequestParamParser.fields;
import static com.github.restup.controller.request.parser.params.ComposedRequestParamParser.filter;
import static com.github.restup.controller.request.parser.params.ComposedRequestParamParser.include;
import static com.github.restup.controller.request.parser.params.ComposedRequestParamParser.limit;
import static com.github.restup.controller.request.parser.params.ComposedRequestParamParser.offset;
import static com.github.restup.controller.request.parser.params.ComposedRequestParamParser.pageNumber;
import static com.github.restup.controller.request.parser.params.ComposedRequestParamParser.sort;
import static com.github.restup.controller.request.parser.params.ParameterParser.ParameterParsers.Bracketed;
import static com.github.restup.util.Streams.forEachNonNull;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.request.parser.params.ComposedRequestParamParser;
import com.github.restup.controller.request.parser.params.ParameterParser;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Defines a parser which handles a request parameter
 *
 * @author abuttaro
 */
public interface RequestParamParser extends Comparable<RequestParamParser> {

    String FIELDS = "fields";
    String FILTER = "filter";
    String INCLUDE = "include";
    String LIMIT = "limit";
    String OFFSET = "offset";
    String PAGE_NUMBER = "pageNumber";
    String SORT = "sort";

    static Builder builder() {
        return new Builder();
    }

    /**
     * @param parameterName name of the parameter, never null
     * @return true if the parser will handle the parameter, false otherwise
     */
    boolean accept(String parameterName);

    /**
     * Parses the parameter as needed and appends appropriate details to the
     * {@link ParsedResourceControllerRequest.Builder}
     *
     * @param request
     * @param builder
     * @param <T>
     *
     * @param parameterName name of the parameter, never null
     * @param parameterValue
     */
    <T> void parse(ResourceControllerRequest request,
        ParsedResourceControllerRequest.Builder<T> builder, String parameterName,
        String... parameterValue);

    /**
     * Rank defines the order in which parsers are applied. greater numbers applied later
     *
     * @return rank
     */
    default int rank() {
        return 0;
    }

    @Override
    default int compareTo(RequestParamParser o) {
        return rank() < o.rank() ? -1 : 1;
    }

    class Builder {

        private ParameterParser[] parameterParsers = {};
        private Set<String> pageOffsetParamName = new HashSet<>();
        private Set<String> pageLimitParamName = new HashSet<>();
        private Set<String> sortParamName = new HashSet<>();
        private Set<String> filterParamName = new HashSet<>();
        private Set<String> includeParamName = new HashSet<>();
        private Set<String> fieldsParamName = new HashSet<>();
        private Set<String> pageNumberParamName = new HashSet();

        static Set<String> getOrDefault(Set<String> set, String defaultValue) {
            return set.isEmpty() ? Sets.newHashSet(defaultValue) : set;
        }

        Builder me() {
            return this;
        }

        public Builder greedy() {
            return withDefaults()
                .withLinkedInStylePagination()
                .withTwitterStylePagination()
                .withPageNumberNamed("pageNo", "pageNum")
                .withPageLimitNamed("pageSize")
                .withFilterNamed("f", "q");
        }

        public Builder withDefaults() {
            return withFieldsNamed(FIELDS)
                .withFilterNamed(FILTER)
                .withIncludeNamed(INCLUDE)
                .withPageLimitNamed(LIMIT)
                .withPageOffsetNamed(OFFSET)
                .withSortNamed(SORT);
        }

        public Builder parameterParsers(ParameterParser... parameterParsers) {
            this.parameterParsers = parameterParsers;
            return me();
        }

        /**
         * withPageOffsetNamed("start").withPageNumberNamed("count")
         */
        public Builder withTwitterStylePagination() {
            return withPageOffsetNamed("start").withPageNumberNamed("count");
        }

        /**
         * withPageLimitNamed("rpp").withPageNumberNamed("page")
         */
        public Builder withLinkedInStylePagination() {
            return withPageLimitNamed("rpp").withPageNumberNamed("page");
        }

        public Builder withPageOffsetNamed(String... names) {
            pageOffsetParamName.addAll(Arrays.asList(names));
            return me();
        }

        public Builder withPageLimitNamed(String... names) {
            pageLimitParamName.addAll(Arrays.asList(names));
            return me();
        }

        public Builder withSortNamed(String... names) {
            sortParamName.addAll(Arrays.asList(names));
            return me();
        }

        public Builder withFilterNamed(String... names) {
            filterParamName.addAll(Arrays.asList(names));
            return me();
        }

        public Builder withIncludeNamed(String... names) {
            includeParamName.addAll(Arrays.asList(names));
            return me();
        }

        public Builder withFieldsNamed(String... names) {
            fieldsParamName.addAll(Arrays.asList(names));
            return me();
        }

        public Builder withPageNumberNamed(String... names) {
            pageNumberParamName.addAll(Arrays.asList(names));
            return me();
        }

        public ParameterParserChain build() {

            List<RequestParamParser> requestParamParsers = new ArrayList<>();

            Set<String> pageOffset = getOrDefault(pageOffsetParamName, OFFSET);
            Set<String> pageLimit = getOrDefault(pageLimitParamName, LIMIT);
            Set<String> sort = getOrDefault(sortParamName, SORT);
            Set<String> filter = getOrDefault(filterParamName, FILTER);
            Set<String> include = getOrDefault(includeParamName, INCLUDE);
            Set<String> fields = getOrDefault(fieldsParamName, FIELDS);
            Set<String> pageNumber = pageNumberParamName;

            List<ComposedRequestParamParser.Builder> paramParserBuilders = new ArrayList<>();
            forEachNonNull(pageOffset, s -> paramParserBuilders.add(offset(s)));
            forEachNonNull(pageLimit, s -> paramParserBuilders.add(limit(s)));
            forEachNonNull(pageNumber, s -> paramParserBuilders.add(pageNumber(s)));
            forEachNonNull(fields, s -> paramParserBuilders.add(fields(s)));
            forEachNonNull(include, s -> paramParserBuilders.add(include(s)));
            forEachNonNull(sort, s -> paramParserBuilders.add(sort(s)));

            // Default as bracketed
            ParameterParser[] parsers = parameterParsers;
            if (parsers.length == 0) {
                parsers = new ParameterParser[]{Bracketed};
            }
            for (ParameterParser parameterParser : parsers) {

                forEachNonNull(pageOffset,
                    s -> paramParserBuilders.add(offset(s, parameterParser)));
                forEachNonNull(pageLimit,
                    s -> paramParserBuilders.add(limit(s, parameterParser)));
                forEachNonNull(pageNumber,
                    s -> paramParserBuilders.add(pageNumber(s, parameterParser)));
                forEachNonNull(fields,
                    s -> paramParserBuilders.add(fields(s, parameterParser)));
                forEachNonNull(include,
                    s -> paramParserBuilders.add(include(s, parameterParser)));
                forEachNonNull(sort,
                    s -> paramParserBuilders.add(sort(s, parameterParser)));
                forEachNonNull(filter,
                    s -> paramParserBuilders.add(filter(s, parameterParser)));
            }

            paramParserBuilders.forEach(b -> requestParamParsers.add(b.build()));
            Collections.sort(requestParamParsers);
            return ParameterParserChain.of(requestParamParsers);
        }
    }
}
