package com.github.restup.errors;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.github.restup.path.DataPathValue;
import com.github.restup.path.MappedFieldPathValue;
import com.github.restup.path.ResourcePath;
import com.github.restup.registry.Resource;

/**
 * A request error, providing necessary details for JSON API errors.
 *
 * @author andy.buttaro
 */
public interface RequestError {

    String getId();

    String getCode();

    String getTitle();

    String getDetail();

    ErrorSource getSource();

    Object getMeta();

    String getStatus();

    int getHttpStatus();


    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ResourcePath path) {
        return builder().source(path);
    }

    public static Builder builder(Resource<?, ?> resource) {
        return builder().resource(resource);
    }

    public static Builder error(Resource<?, ?> resource, Throwable t) {
        return new Builder(t)
                .status(ErrorCodeStatus.INTERNAL_SERVER_ERROR)
                .resource(resource);
    }

    public static ErrorObjectException buildException(Resource<?, ?> resource, Throwable t) {
        return error(resource, t).buildException();
    }

    public static ErrorObjectException buildException(Throwable t) {
        return error(null, t).buildException();
    }

    public static void throwError(Throwable t) {
        error(null, t).throwError();
    }

    public class Builder {

        // TODO doc

        private String[] codePrefix;
        private String[] codeSuffix;
        private ResourcePath path;
        private Resource<?, ?> resource;
        private ErrorCodeStatus status;
        private String id;
        private ErrorCode errorCode;
        private String code;
        private String title;
        private String detail;
        private ErrorSource source;
        private Object meta;
        private String detailPattern;
        private Object[] detailPatternArgs;
        private Throwable cause;
        private int httpStatus;

        private Builder() {}

        private Builder(Throwable t) {
            this.cause = t;
        }

        private Builder me() {
            return this;
        }

        public Builder httpStatus(int httpStatus) {
            this.httpStatus = httpStatus;
            return me();
        }

        public Builder code(String code) {
            this.code = code;
            return me();
        }

        public Builder code(ErrorCode code) {
            this.errorCode = code;
            return me();
        }

        public Builder detail(String detail) {
            return detail(detail, null, null);
        }

        public Builder detail(String pattern, Object... args) {
            return detail(MessageFormat.format(pattern, args), pattern, args);
        }

        private Builder detail(String detail, String pattern, Object[] args) {
            this.detail = detail;
            this.detailPattern = pattern;
            this.detailPatternArgs = args;
            return me();
        }

        public Builder status(ErrorCodeStatus status) {
            this.status = status;
            return me();
        }

        public Builder id(String id) {
            this.id = id;
            return me();
        }

        public Builder meta(Object meta) {
            this.meta = meta;
            return me();
        }

        public Builder source(ErrorSource source) {
            this.source = source;
            return me();
        }

        public Builder source(ResourcePath source) {
            return path(source);
        }

        public Builder path(ResourcePath path) {
            this.path = path;
            return resource(path.getResource());
        }

        public Builder title(String title) {
            this.title = title;
            return me();
        }

        public Builder codePrefix(String... codePrefix) {
            this.codePrefix = codePrefix;
            return me();
        }

        public Builder codeSuffix(String... codeSuffix) {
            this.codeSuffix = codeSuffix;
            return me();
        }

        public Builder resource(Resource<?, ?> resource) {
            this.resource = resource;
            return me();
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        public Builder meta(String key, Object value) {
            if (!(meta instanceof Map)) {
                if (meta == null) {
                    meta = new HashMap();
                } else {
                    throw new IllegalArgumentException("Unable to set Meta with key value pairs");
                }
            }
            Map<String, Object> map = (Map) meta;
            map.put(key, value);
            return me();
        }

        @SuppressWarnings("rawtypes")
        private String getDefaultCode() {
            if (status == ErrorCodeStatus.INTERNAL_SERVER_ERROR) {
                return ErrorCodeStatus.INTERNAL_SERVER_ERROR.name();
            }

            if (errorCode != null) {
                return errorCode.name();
            }

            if (status != null) {
                return status.name();
            }

            List<String> parts = new ArrayList<String>();
            if (codePrefix != null) {
                for (String prefix : codePrefix) {
                    parts.add(prefix);
                }
            }
            if (resource != null) {
                parts.add(resource.getName());
            }
            if (path != null) {
                ResourcePath current = path.first(MappedFieldPathValue.class);
                while (current != null) {
                    if (current.value() instanceof MappedFieldPathValue) {
                        MappedFieldPathValue pv = (MappedFieldPathValue) current.value();
                        if (isNotEmpty(pv.getApiPath())) {
                            String[] camelParts = splitByCharacterTypeCamelCase(pv.getApiPath());
                            for (String s : camelParts) {
                                parts.add(s);
                            }
                        }
                    }
                    current = current.next();
                }
            }
            if (codeSuffix != null) {
                for (String suffix : codeSuffix) {
                    parts.add(suffix);
                }
            } else if (codePrefix == null) {
                parts.add("ERROR");
            }

            return join(parts, "_").toUpperCase();
        }

        public ErrorObjectException buildException() {
            RequestError err = build();
            return new ErrorObjectException(err);
        }

        public void throwError() throws ErrorObjectException {
            throw buildException();
        }

        private ErrorSource path() {
            if (path != null) {
                if (path.first(DataPathValue.class) == null) {
                    return ResourcePath.data(resource).append(path.first()).setQuiet(true).build();
                }
            }
            return path;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public RequestError build() {
            // default id to new UUID
            String id = this.id;
            if (id == null) {
                id = UUID.randomUUID().toString();
            }
            // default status to 400
            ErrorCodeStatus status = this.status;
            if (status == null) {
                status = ErrorCodeStatus.BAD_REQUEST;
            }
            int httpStatus = this.httpStatus;
            if (httpStatus == 0) {
                httpStatus = status.getHttpStatus();
            }

            // if source is null, set it to path
            ErrorSource source = this.source;
            if (source == null) {
                source = path();
            }
            Object meta = this.meta;
            if (meta == null
                    && (resource != null
                            || source instanceof ParameterError)) {
                meta = new HashMap<String, Object>();
            }
            // if meta is a Map, add some defaults
            if (meta instanceof Map) {
                Map<String, Object> map = (Map) meta;
                // resource name
                if (resource != null) {
                    map.put("resource", resource.getName());
                }
                // parameter detail
                if (source instanceof ParameterError) {
                    ParameterError err = (ParameterError) source;
                    map.put("parameterName", err.getParameterName());
                    map.put("parameterValue", err.getParameterValue());
                }
            }
            String code = this.code;
            if (isEmpty(code)) {
                code = getDefaultCode();
            }
            String title = this.title;
            if (isEmpty(title)) {
                if (errorCode != null) {
                    title = errorCode.getTitle();
                } else {
                    title = status.getDefaultTitle();
                }
            }
            String detail = this.detail;
            if (isEmpty(detail)) {
                if (errorCode != null) {
                    detail = errorCode.getDetail();
                } else {
                    detail = status.getDefaultDetail();
                }
            }

            return new BasicRequestError(id, code, title, detail, detailPattern, detailPatternArgs, source, meta, httpStatus, cause);
        }

    }
}
