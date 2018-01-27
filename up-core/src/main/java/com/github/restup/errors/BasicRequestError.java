package com.github.restup.errors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default {@link RequestError} implementation
 */
class BasicRequestError implements RequestError, DebugRequestError {

    private final static Logger log = LoggerFactory.getLogger(BasicRequestError.class);

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

    
    BasicRequestError(String id, String code, String title, String detail, String detailPattern, Object[] detailPatternArgs, ErrorSource source, Object meta, int httpStatus, Throwable cause, StackTraceElement[] stackTrace) {
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
        this.stackTrace = stackTrace;
    }
    
    /**
     * Since {@link RequestError}s are added to {@link Errors} and often thrown as detail in {@link ErrorObjectException}, the stack detail of the {@link ErrorObjectException} is not meaningful.  So, when this {@link RequestError} is instantiated and DEBUG logging is enabled, the stacktrace from the current Thread is captured for later logging using {@link #logStackTrace()}
     */
    BasicRequestError(String id, String code, String title, String detail, String detailPattern, Object[] detailPatternArgs, ErrorSource source, Object meta, int httpStatus, Throwable cause) {
        this(id, code, title, detail, detailPattern, detailPatternArgs, source, meta, httpStatus, cause, getStack(cause, log));
    }
    
    static StackTraceElement[] getStack(Throwable cause, Logger log) {
        if (cause == null && log.isDebugEnabled()) {
            return Thread.currentThread().getStackTrace();
        }
        return null;
    }
    
    /**
     * logs the stacktrace relative to the occurrence of this error
     */
    @Override
    public Object logStackTrace() {
        if (stackTrace != null) {
            StackDetail result = new StackDetail(detail, stackTrace);
            log.debug(title, result);
            return result;
        } else if (cause != null) {
            log.error(title, cause);
            return cause;
        } else {
            log.debug(title);
            return title;
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDetail() {
        return detail;
    }

    @Override
    public ErrorSource getSource() {
        return source;
    }

    @Override
    public Object getMeta() {
        return meta;
    }

    @Override
    public String getStatus() {
        return String.valueOf(httpStatus);
    }

    @Override
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
    final static class StackDetail extends Exception {

        public StackDetail(String title, StackTraceElement[] stack) {
            super(title);
            this.setStackTrace(stack);
        }

    }
}
