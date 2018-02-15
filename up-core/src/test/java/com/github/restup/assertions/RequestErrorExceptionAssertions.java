package com.github.restup.assertions;

import java.util.Map;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.Assertions;
import com.github.restup.errors.RequestErrorException;
import com.github.restup.errors.RequestError;

public class RequestErrorExceptionAssertions<ASSERT extends AbstractThrowableAssert<ASSERT, RequestErrorException>> extends DelegatingAbstractThrowableAssertions<RequestErrorExceptionAssertions<ASSERT>,ASSERT,RequestErrorException> {

    private final RequestErrorException exception;
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    RequestErrorExceptionAssertions(Throwable thrownException) {
        super((AbstractThrowableAssert)Assertions.assertThat(thrownException).isInstanceOf(RequestErrorException.class));
        exception = (RequestErrorException) thrownException;
    }

    public RequestErrorExceptionAssertions<ASSERT> httpStatus(int httpStatus) {
        assertThat(exception.getHttpStatus()).isEqualTo(httpStatus);
        return me();
    }

    public RequestErrorExceptionAssertions<ASSERT> code(String code) {
        assertThat(exception.getCode()).isEqualTo(code);
        return me();
    }

    public RequestErrorExceptionAssertions<ASSERT> detail(String detail) {
        return detail(0, detail);
    }

    public RequestErrorExceptionAssertions<ASSERT> detail(int i, String detail) {
        assertThat(getError(i).getDetail()).isEqualTo(detail);
        return me();
    }

    public RequestErrorExceptionAssertions<ASSERT> meta(String key, Object value) {
        return meta(0, key, value);
    }

    public RequestErrorExceptionAssertions<ASSERT> meta(int i, String key, Object value) {
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
