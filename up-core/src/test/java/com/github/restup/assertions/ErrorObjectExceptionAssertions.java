package com.github.restup.assertions;

import java.util.Map;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.Assertions;
import com.github.restup.errors.ErrorObjectException;
import com.github.restup.errors.RequestError;

public class ErrorObjectExceptionAssertions<ASSERT extends AbstractThrowableAssert<ASSERT, ErrorObjectException>> extends DelegatingAbstractThrowableAssertions<ErrorObjectExceptionAssertions<ASSERT>,ASSERT,ErrorObjectException> {

    private final ErrorObjectException exception;
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    ErrorObjectExceptionAssertions(Throwable thrownException) {
        super((AbstractThrowableAssert)Assertions.assertThat(thrownException).isInstanceOf(ErrorObjectException.class));
        exception = (ErrorObjectException) thrownException;
    }

    public ErrorObjectExceptionAssertions<ASSERT> httpStatus(int httpStatus) {
        assertThat(exception.getHttpStatus()).isEqualTo(httpStatus);
        return me();
    }

    public ErrorObjectExceptionAssertions<ASSERT> code(String code) {
        assertThat(exception.getCode()).isEqualTo(code);
        return me();
    }

    public ErrorObjectExceptionAssertions<ASSERT> detail(String detail) {
        return detail(0, detail);
    }

    public ErrorObjectExceptionAssertions<ASSERT> detail(int i, String detail) {
        assertThat(getError(i).getDetail()).isEqualTo(detail);
        return me();
    }

    public ErrorObjectExceptionAssertions<ASSERT> meta(String key, Object value) {
        return meta(0, key, value);
    }

    public ErrorObjectExceptionAssertions<ASSERT> meta(int i, String key, Object value) {
        assertThat(getMeta(i).get(key)).isEqualTo(value);
        return me();
    }
    
    private RequestError getError(int i) {
        return exception.getErrors().get(i);
    }
    
    @SuppressWarnings("unchecked")
    private Map<String,Object> getMeta(int i) {
        Object meta = getError(i).getMeta();
        if ( meta instanceof Map ) {
            return (Map<String,Object>) meta;
        }
        throw new IllegalStateException("Meta is not a map");
    }
}
