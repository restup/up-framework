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
 * {@link RequestError} Builder
 */
public class ErrorBuilder {

    //TODO doc

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

    private ErrorBuilder() {
    }

    private ErrorBuilder(Throwable t) {
        this.cause = t;
    }

    public static ErrorBuilder builder() {
        return new ErrorBuilder();
    }

    public static ErrorBuilder builder(ResourcePath path) {
        return builder().source(path);
    }

    public static ErrorBuilder builder(Resource<?, ?> resource) {
        return builder().resource(resource);
    }

    public static ErrorBuilder error(Resource<?, ?> resource, Throwable t) {
        return new ErrorBuilder(t)
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

    private ErrorBuilder me() {
        return this;
    }

    public ErrorBuilder code(String code) {
        this.code = code;
        return me();
    }

    public ErrorBuilder code(ErrorCode code) {
        this.errorCode = code;
        return me();
    }

    public ErrorBuilder detail(String detail) {
        return detail(detail, null, null);
    }

    public ErrorBuilder detail(String pattern, Object... args) {
        return detail(MessageFormat.format(pattern, args), pattern, args);
    }

    private ErrorBuilder detail(String detail, String pattern, Object[] args) {
        this.detail = detail;
        this.detailPattern = pattern;
        this.detailPatternArgs = args;
        return me();
    }

    public ErrorBuilder status(ErrorCodeStatus status) {
        this.status = status;
        return me();
    }

    public ErrorBuilder id(String id) {
        this.id = id;
        return me();
    }

    public ErrorBuilder meta(Object meta) {
        this.meta = meta;
        return me();
    }

    public ErrorBuilder source(ErrorSource source) {
        this.source = source;
        return me();
    }

    public ErrorBuilder source(ResourcePath source) {
        return path(source);
    }

    public ErrorBuilder path(ResourcePath path) {
        this.path = path;
        return resource(path.getResource());
    }

    public ErrorBuilder title(String title) {
        this.title = title;
        return me();
    }

    public ErrorBuilder codePrefix(String... codePrefix) {
        this.codePrefix = codePrefix;
        return me();
    }

    public ErrorBuilder codeSuffix(String... codeSuffix) {
        this.codeSuffix = codeSuffix;
        return me();
    }

    public ErrorBuilder resource(Resource<?, ?> resource) {
        this.resource = resource;
        return me();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public ErrorBuilder meta(String key, Object value) {
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
        if (resource == null && source instanceof ResourcePath) {
            ResourcePath path = (ResourcePath) source;
            resource = path.getResource();
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

        return new DefaultRequestError(id, code, title, detail, detailPattern, detailPatternArgs, source, meta, httpStatus, cause);
    }

    public enum ErrorCodeStatus {
        INTERNAL_SERVER_ERROR(500, "Unexpected Error", "The server encountered an unexpected condition which prevented it from fulfilling the request."),
        BAD_REQUEST(400, "Bad Request", "The request could not be understood by the server due to malformed syntax"),
        FORBIDDEN(403, "Forbidden", "The request is forbidden˙"),
        NOT_FOUND(404, "Not Found", "The requested resource does not exist"),
        UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type", "Media type is not supported"),
        METHOD_NOT_ALLOWED(405, "Method Not Allowed", "The method specified in the Request-Line is not allowed for the resource identified by the Request-URI.");

        final private int httpStatus;
        final private String defaultTitle;
        final private String defaultDetail;

        private ErrorCodeStatus(int httpStatus, String defaultTitle, String defaultDetail) {
            this.httpStatus = httpStatus;
            this.defaultTitle = defaultTitle;
            this.defaultDetail = defaultDetail;
        }

        public int getHttpStatus() {
            return httpStatus;
        }

        public String getDefaultTitle() {
            return defaultTitle;
        }

        public String getDefaultDetail() {
            return defaultDetail;
        }
    }

    public enum ErrorCode {
        BODY_REQUIRED("Body is required", "Body may not be empty"),
        BODY_ARRAY_NOT_SUPPORTED("Body array is not supported"),
        BODY_INVALID("Unable to deserialize body"),
        ID_NOT_ALLOWED_ON_CREATE("Id not allowed", "Id is auto generated and may not be specified"),
        UNKNOWN_RESOURCE("Unknown resource"),
        UNEXPECTED_FIND_RESULTS("Find returned more than one result"),
        SERIALIZATION_ERROR("Unable to serialize response"),
        INVALID_RELATIONSHIP("Invalid Relationship"),
        ID_REQUIRED("id is required", "Missing identifier at specified path"),
        TYPE_REQUIRED("type is required", "Missing type at specified path"),
        WRAP_FIELDS_WITH_ATTRIBUTES("field must be wrapped", "The field is valid, but it must be wrapped in attributes");

        private final String title;
        private final String detail;

        ErrorCode(String title, String detail) {
            this.title = title;
            this.detail = detail;
        }

        ErrorCode(String title) {
            this(title, title);
        }

        public String getTitle() {
            return title;
        }

        public String getDetail() {
            return detail;
        }
    }

}
