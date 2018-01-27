package com.github.restup.errors;

import java.util.Arrays;
import org.junit.Test;
import com.github.restup.assertions.Assertions;

public class ErrorObjectExceptionTest {
    
    @Test
    public void testErrorObjectException() {
        Assertions.assertThat(new ErrorObjectException(new IllegalArgumentException()))
            .httpStatus(500)
            .code("INTERNAL_SERVER_ERROR");
    }
    
    @Test
    public void testComparator() {
        Assertions.assertThat(new ErrorObjectException(Arrays.asList(
                notFound(), exception())))
            .httpStatus(500)
            .code("INTERNAL_SERVER_ERROR");

        Assertions.assertThat(new ErrorObjectException(Arrays.asList(
                exception(), notFound())))
            .httpStatus(500)
            .code("INTERNAL_SERVER_ERROR");

    }
    
    @Test
    public void testComparatorEquals() {
        Assertions.assertThat(new ErrorObjectException(Arrays.asList(
                notFound(), notFound())))
            .httpStatus(404)
            .code("NOT_FOUND");
    }

    private RequestError exception() {
        return RequestError.builder().status(ErrorCodeStatus.INTERNAL_SERVER_ERROR).build();
    }

    private RequestError notFound() {
        return RequestError.builder().status(ErrorCodeStatus.NOT_FOUND).build();
    }

}
