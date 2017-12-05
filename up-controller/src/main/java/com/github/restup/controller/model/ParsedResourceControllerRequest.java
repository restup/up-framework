package com.github.restup.controller.model;

import static com.github.restup.util.UpUtils.unmodifiableList;

import com.github.restup.bind.converter.ParameterConverter;
import com.github.restup.bind.converter.ParameterConverterFactory;
import com.github.restup.errors.ErrorBuilder;
import com.github.restup.errors.ErrorBuilder.ErrorCode;
import com.github.restup.errors.ErrorFactory;
import com.github.restup.errors.Errors;
import com.github.restup.errors.RequestError;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.path.ResourcePath;
import com.github.restup.query.ResourceQueryStatement;
import com.github.restup.query.ResourceQueryStatement.Type;
import com.github.restup.query.ResourceSort;
import com.github.restup.query.criteria.ResourcePathFilter;
import com.github.restup.query.criteria.ResourcePathFilter.Operator;
import com.github.restup.query.criteria.ResourceQueryCriteria;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.service.model.ResourceData;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.collections4.list.SetUniqueList;

/**
 * Contains result of parsing parameters
 */
public class ParsedResourceControllerRequest<T> extends ResourceControllerRequest {

    private final T data;
    private final List<ResourcePath> dataPaths;
    private final List<ResourceQueryStatement> requestedQueries;
    private final ResourceControllerRequest request;
    private final List<String> acceptedParameterNames;
    private final String pageLimitParameterName;
    private final String pageOffsetParameterName;
    private final boolean pageOffsetOneBased;

    public ParsedResourceControllerRequest(T data, List<ResourcePath> requestedPaths,
            List<ResourceQueryStatement> requestedQueries, ResourceControllerRequest request, ResourceData<?> body, List<String> acceptedParameterNames, String pageLimitParameterName, String pageOffsetParameterName, boolean pageOffsetOneBased) {
        super(request.getMethod(), request.getResource(), request.getIds(), request.getRelationship(), request.getResourceRelationship(), body, request.getContentType(), request.getBaseRequestUrl(), request.getRequestUrl());
        this.data = data;
        this.dataPaths = unmodifiableList(requestedPaths);
        this.requestedQueries = unmodifiableList(requestedQueries);
        this.request = request;
        this.acceptedParameterNames = acceptedParameterNames;
        this.pageLimitParameterName = pageLimitParameterName;
        this.pageOffsetParameterName = pageOffsetParameterName;
        this.pageOffsetOneBased = pageOffsetOneBased;
    }

    public static <T> Builder<T> builder(ResourceRegistry registry, ResourceControllerRequest request) {
        return new Builder<T>(registry, request);
    }

    public T getData() {
        return data;
    }

    public List<ResourcePath> getRequestedPaths() {
        return dataPaths;
    }

    public List<ResourceQueryStatement> getRequestedQueries() {
        return requestedQueries;
    }

    public List<String> getParameterNames() {
        return request.getParameterNames();
    }

    public String[] getParameter(String parameterName) {
        return request.getParameter(parameterName);
    }

    public Enumeration<String> getHeaders(String name) {
        return request == null ? null : request.getHeaders(name);
    }

    public String getPageLimitParameterName() {
        return pageLimitParameterName;
    }

    public String getPageOffsetParameterName() {
        return pageOffsetParameterName;
    }

    public List<String> getAcceptedParameterNames() {
        return acceptedParameterNames;
    }

    public boolean isPageOffsetOneBased() {
        return pageOffsetOneBased;
    }

    public static class Builder<T> implements Errors {

        private final ResourceRegistry registry;
        private final ResourceControllerRequest request;
        private ResourceData<T> body;
        private T data;
        private List<ResourcePath> requestedPaths;
        private Map<String, ResourceQueryStatement.Builder> resourceQueries;

        private List<String> acceptedParameterNames;
        private String pageLimitParameterName;
        private String pageOffsetParameterName;
        private boolean pageOffsetOneBased;
        private Integer pageLimit;
        private Integer pageOffset;
        private Errors errors;

        private int maxPageSize;

        public Builder(ResourceRegistry registry, ResourceControllerRequest request) {
            super();
            maxPageSize = 100;
            this.request = request;
            this.registry = registry;
            acceptedParameterNames = new ArrayList<String>();
        }

        private Builder<T> me() {
            return this;
        }

        public T getData() {
            return data;
        }

        public Builder<T> setData(T data) {
            this.data = data;
            return me();
        }

        private ResourcePath.Builder builder() {
            return builder(this.getResource());
        }

        private ResourcePath.Builder builder(Resource<?, ?> resource) {
            return ResourcePath.builder(resource).setQuiet(true);
        }

        private ResourcePath path(Integer index, MappedField<?> field) {
            return builder().data(index).append(field).build();
        }

        private ResourcePath path(MappedField<?> field) {
            return builder().append(field).build();
        }

        private ResourcePath path(Resource<?, ?> resource, String field) {
            return builder(resource).path(field).build();
        }

        public Builder<T> addRequestedPath(Integer index, MappedField<?> field) {
            if (index == null) {
                return addRequestedPath(field);
            }
            return addRequestedPath(path(index, field));
        }

        public Builder<T> addRequestedPath(MappedField<?> field) {
            return addRequestedPath(path(field));
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public Builder<T> addRequestedPath(ResourcePath path) {
            if (requestedPaths == null) {
                requestedPaths = SetUniqueList.setUniqueList(new ArrayList());
            }
            requestedPaths.add(path);
            return me();
        }

        public Builder<T> addAcceptedParameterName(String p) {
            acceptedParameterNames.add(p);
            return me();
        }

        public Builder<T> addSort(String rawParameterName, String rawParameterValue, String field, Boolean asc) {
            return addSort(rawParameterName, rawParameterValue, getResource(), field, asc);
        }

        private Builder<T> addSort(String rawParameterName, String rawParameterValue, Resource<?, ?> resource,
                String field, Boolean asc) {
            ResourcePath path = path(resource, field);
            if (!validatePath(rawParameterName, rawParameterValue, path, resource, field)) {
                return me();
            }
            return addSort(new ResourceSort(path, asc));
        }

        public void addFilter(List<ResourcePath> paths, Collection<Object> joinIds) {
            ResourceQueryStatement.Builder builder = getOrCreateQuery(getResource());
            builder.addCriteria(paths, joinIds);
        }

        private Builder<T> addFilter(ResourceQueryCriteria filter) {
            ResourceQueryStatement.Builder builder = getOrCreateQuery(getResource());
            builder.addCriteria(filter);
            return me();
        }

        private Builder<T> addSort(ResourceSort resourceSort) {
            ResourceQueryStatement.Builder builder = getOrCreateQuery(getResource());
            builder.addSort(resourceSort);
            return me();
        }

        public Builder<T> setFieldRequest(String rawParameterName, String rawParameterValue, Resource<?, ?> resource,
                Type type) {
            ResourceQueryStatement.Builder builder = getOrCreateQuery(resource);
            return setType(rawParameterName, rawParameterValue, builder, type);
        }

        public Builder<T> setFieldRequest(String rawParameterName, String rawParameterValue, String resource,
                Type type) {
            ResourceQueryStatement.Builder builder = getOrCreateQuery(rawParameterName, rawParameterValue, resource);
            return setType(rawParameterName, rawParameterValue, builder, type);
        }

        private Builder<T> setType(String rawParameterName, String rawParameterValue,
                ResourceQueryStatement.Builder builder, Type type) {
            if (builder != null) {
                builder.setType(type);
            }
            return me();
        }

        public Builder<T> addIncludeJoinPaths(String rawParameterName, String rawParameterValue, String resource,
                String field) {
            ResourceQueryStatement.Builder builder = getOrCreateQuery(rawParameterName, rawParameterValue, resource);
            if (builder != null) {
                builder.addIncludeJoinPaths(getResource(), field);
            }
            return me();
        }

        public Builder<T> addRequestedField(String rawParameterName, String rawParameterValue, String resource,
                String field) {
            // TODO handle *,**, +, -, embedded fields, embedded paths
            ResourceQueryStatement.Builder builder = getOrCreateQuery(rawParameterName, rawParameterValue, resource);
            if (builder != null) {
                builder.addRequestedPaths(field);
            }
            return me();
        }

        public Builder<T> addAdditionalField(String rawParameterName, String rawParameterValue, String resource,
                String field) {
            ResourceQueryStatement.Builder builder = getOrCreateQuery(rawParameterName, rawParameterValue, resource);
            if (builder != null) {
                builder.addRequestedPathsAdded(field);
            }
            return me();
        }

        public Builder<T> addExcludedField(String rawParameterName, String rawParameterValue, String resource,
                String field) {
            ResourceQueryStatement.Builder builder = getOrCreateQuery(rawParameterName, rawParameterValue, resource);
            if (builder != null) {
                builder.addRequestedPathsExcluded(field);
            }
            return me();
        }

        private Resource<?, ?> getResource(String rawParameterName, String rawParameterValue, String resourceName) {
            Resource<?, ?> resource = registry.getResource(resourceName);
            if (resource == null) {
                addError(getParameterError(rawParameterName, rawParameterValue).code(ErrorCode.UNKNOWN_RESOURCE)
                        .detail("Unknown resource specified for parameter {0}", rawParameterName));
            }
            return resource;
        }

        private ResourceQueryStatement.Builder getOrCreateQuery(String rawParameterName, String rawParameterValue,
                String resourceName) {
            ResourceQueryStatement.Builder builder = getQuery(resourceName);
            if (builder == null) {
                Resource<?, ?> resource = getResource(rawParameterName, rawParameterValue, resourceName);
                builder = getOrCreateQuery(resource);
            }
            return builder;
        }

        private ResourceQueryStatement.Builder getOrCreateQuery(Resource<?, ?> resource) {
            ResourceQueryStatement.Builder builder = getQuery(resource);
            if (builder == null && resource != null) {
                builder = ResourceQueryStatement.builder(resource, this);
                if (resourceQueries == null) {
                    resourceQueries = new HashMap<String, ResourceQueryStatement.Builder>();
                }
                resourceQueries.put(resource.getName(), builder);
            }
            return builder;
        }

        private ResourceQueryStatement.Builder getQuery(Resource<?, ?> resource) {
            ResourceQueryStatement.Builder builder = null;
            if (resource != null) {
                return getQuery(resource.getName());
            }
            return builder;
        }

        private ResourceQueryStatement.Builder getQuery(String resourceName) {
            return resourceQueries == null ? null : resourceQueries.get(resourceName);
        }

        public Builder<T> addFilter(String rawParameterName, String rawParameterValue, String field, String operator,
                String value) {
            Operator op = Operator.of(operator);
            if (op == null) {
                addError(getParameterError(rawParameterName, value).code("INVALID_OPERATOR")
                        .title("Invalid operator specified ")
                        .detail("{0} specifies an invalid operator, '{1}'", rawParameterName, operator));
                return me();
            }
            return addFilter(rawParameterName, rawParameterValue, field, op, value);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public Builder<T> addFilter(String rawParameterName, Object rawParameterValue, String field, Operator operator,
                Object value) {
            Resource<?, ?> resource = getResource();
            ResourcePath path = path(resource, field);
            if (!validatePath(rawParameterName, rawParameterValue, path, resource, field)) {
                return me();
            }
            // TODO check if field supports operator
            Object converted = convertValue(path, value, rawParameterName);
            return addFilter(new ResourcePathFilter(path, operator, converted));
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        private Object convertValue(ResourcePath path, Object value, String parameterName) {
            if (value != null) {
                MappedField<?> mf = path.lastMappedField();
                if (mf != null) {
                    Class<?> type = mf.getType();

                    ParameterConverterFactory factory = registry.getSettings().getParameterConverterFactory();
                    ParameterConverter converter = factory.getConverter(value.getClass(), type);
                    if (converter != null) {
                        return converter.convert(parameterName, value, this);
                    }
                }
            }
            return value;
        }

        private <ID extends Serializable> boolean validatePath(String rawParameterName, Object rawParameterValue,
                ResourcePath path, Resource<?, ?> resource, String field) {
            if (!path.isValid()) {
                addError(getParameterError(rawParameterName, rawParameterValue).code("INVALID_PARAMETER_PATH")
                        .title("Invalid path specified ")
                        .detail("{0} specifies an invalid field, '{1}', for {2} resources", rawParameterName, field,
                                resource));
                return false;
            }
            return true;
        }

        public Builder<T> setPageLimit(String parameterName, Integer value) {
            if (this.pageLimit != null) {
                addDuplicateParameter(parameterName, value, pageLimitParameterName, pageLimit);
            } else if (hasMinError(parameterName, 0, value)) {
                return me();
            } else if (hasMaxError(parameterName, maxPageSize, value)) {
                // XXX maybe should be using javax validations here.. but max page size is
                // likely varied
                // by resource or even request if you wanted to consider # of includes & fields
                // TODO resource based max page size
                return me();
            } else {
                this.pageLimit = value;
                this.pageLimitParameterName = parameterName;
                ResourceQueryStatement.Builder builder = getOrCreateQuery(getResource());
                builder.setPageLimit(value);
            }
            return me();
        }

        public Builder<T> setPageOffset(String parameterName, Integer value) {
            return setPageOffset(parameterName, value, false);
        }

        public Builder<T> setPageOffset(String parameterName, Integer value, boolean pageOffsetAsOneBased) {
            this.pageOffsetOneBased = pageOffsetAsOneBased;
            if (this.pageOffset != null) {
                addDuplicateParameter(parameterName, value, pageOffsetParameterName, pageOffset);
            } else if (hasMinError(parameterName, 0, value)) {
                return me();
            } else {
                // TODO validate pageOffset is sensible
                this.pageOffset = value;
                this.pageOffsetParameterName = parameterName;
                ResourceQueryStatement.Builder builder = getOrCreateQuery(getResource());
                builder.setPageOffset(value);
            }
            return me();
        }

        private boolean hasMaxError(String parameterName, int maxValue, Integer actualValue) {
            if (actualValue > maxValue) {
                addError(getParameterError(parameterName, actualValue)
                        // TODO split camelcase
                        .code("MAX_" + parameterName.toUpperCase()).title("Max exceeded")
                        .detail("Maximum allowable {0} is {1}", parameterName, maxValue));
                return true;
            }
            return false;
        }

        private boolean hasMinError(String parameterName, int minValue, Integer actualValue) {
            if (actualValue == null || actualValue < minValue) {
                addError(getParameterError(parameterName, actualValue)
                        // TODO split camelcase
                        .code("MIN_" + parameterName.toUpperCase()).title("Minimum error") // There is no english
                        // opposite of
                        // exceed :)
                        // Neologism: deceed
                        .detail("Minimum allowable value for {0} is {1}", parameterName, minValue));
                return true;
            }
            return false;
        }

        private void addDuplicateParameter(String parameterName, Integer value, String existingParameterName,
                Integer existingValue) {
            addError(getParameterError(parameterName, value).code("DUPLICATE_PARAMETER")
                    .title("Parameter value already specified")
                    .detail("{0} was already specified using '{1}={2}'", parameterName, existingParameterName,
                            String.valueOf(existingValue))
                    .meta("existingParameterName", existingParameterName).meta("existingValue", existingValue));
        }

        public void addParameterError(String parameterName, Object value) {
            addError(getParameterError(parameterName, value));
        }

        private ErrorBuilder getParameterError(String parameterName, Object value) {
            return ErrorBuilder.builder().code("INVALID_PARAMETER").title("Invalid parameter value")
                    .detail("'{0}' is not a valid value for {1}", value, parameterName)
                    .source(getErrorFactory().createParameterError(parameterName, value));
        }

        public ErrorFactory getErrorFactory() {
            return registry.getSettings().getErrorFactory();
        }

        private Resource<?, ?> getResource() {
            return request.getResource();
        }

        public void addError(ErrorBuilder b) {
            if (b != null) {
                if (errors == null) {
                    errors = getErrorFactory().createErrors();
                }
                errors.addError(b.resource(getResource()));
            }
        }

        public void addError(ErrorCode code) {
            addError(ErrorBuilder.builder().code(code).resource(getResource()));
        }

        public List<RequestError> getErrors() {
            return errors == null ? null : errors.getErrors();
        }

        public boolean hasErrors() {
            return errors == null ? false : errors.hasErrors();
        }

        public void assertErrors() {
            if (errors != null) {
                errors.assertErrors();
            }
        }

        public ParsedResourceControllerRequest<T> build() {
            assertErrors();
            List<ResourceQueryStatement> queries = null;
            if (resourceQueries != null) {
                queries = new ArrayList<ResourceQueryStatement>(resourceQueries.size());
                for (ResourceQueryStatement.Builder b : resourceQueries.values()) {
                    b.setPagingEnabled(true);
                    queries.add(b.build());
                }
            }
            // add mediaType as requested param so that it is carried through for linking
            // when used
            String[] mediaTypes = request.getParameter(MediaType.PARAM);
            if (mediaTypes != null) {
                for (String s : mediaTypes) {
                    if (Objects.equals(s, request.getContentType())) {
                        addAcceptedParameterName(MediaType.PARAM);
                    }
                }
            }
            ParsedResourceControllerRequest<T> result = new ParsedResourceControllerRequest<T>(data, requestedPaths,
                    queries, request, body, acceptedParameterNames, pageLimitParameterName, pageOffsetParameterName, pageOffsetOneBased);
            return result;
        }

    }

}
