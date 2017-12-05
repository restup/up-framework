package com.github.restup.errors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default {@link RequestError} implementation
 */
class DefaultRequestError implements RequestError, DebugRequestError {

    private final static Logger log = LoggerFactory.getLogger(RequestError.class);

    private final String id;
    private final String code;
    private final String title;
    private final String detail;
    private final ErrorSource source;
    private final Object meta;
    private final int httpStatus;

    private final String detailPattern;
    private final Object[] detailPatternArgs;

    private final Throwable cause;
    private final StackTraceElement[] stackTrace;

    /**
     * Since {@link RequestError}s are added to {@link Errors} and often thrown as detail in {@link ErrorObjectException}, the stack detail of the {@link ErrorObjectException} is not meaningful.  So, when this {@link RequestError} is instantiated and DEBUG logging is enabled, the stacktrace from the current Thread is captured for later logging using {@link #logStackTrace()}
     */
    DefaultRequestError(String id, String code, String title, String detail, String detailPattern, Object[] detailPatternArgs, ErrorSource source, Object meta, int httpStatus, Throwable cause) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.detail = detail;
        this.source = source;
        this.meta = meta;
        this.httpStatus = httpStatus;
        this.detailPattern = detailPattern;
        this.detailPatternArgs = detailPatternArgs;
        this.cause = cause;
        if (cause == null && log.isDebugEnabled()) {
            stackTrace = Thread.currentThread().getStackTrace();
        } else {
            stackTrace = null;
        }
    }

    /**
     * logs the stacktrace relative to the occurrence of this error
     */
    public void logStackTrace() {
        if (stackTrace != null) {
            log.debug(title, new StackDetail(detail, stackTrace));
        } else if (cause != null) {
            log.error(title, cause);
        } else {
            log.debug(title);
        }
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public ErrorSource getSource() {
        return source;
    }

    public Object getMeta() {
        return meta;
    }

    public String getStatus() {
        return String.valueOf(httpStatus);
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getDetailPattern() {
        return detailPattern;
    }

    public Object[] getDetailPatternArgs() {
        return detailPatternArgs;
    }

    public Throwable getCause() {
        return cause;
    }

    public StackTraceElement[] getStackTrace() {
        return stackTrace;
    }

    /**
     * Just to rely on default stack printing for debugging
     */
    @SuppressWarnings("serial")
    private final static class StackDetail extends Exception {

        public StackDetail(String title, StackTraceElement[] stack) {
            super(title);
            this.setStackTrace(stack);
        }

    }
}
