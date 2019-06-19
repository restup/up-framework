package com.github.restup.errors;

import static com.github.restup.assertions.Assertions.assertThrows;

import com.github.restup.annotations.model.StatusCode;
import com.github.restup.assertions.Assertions;
import java.util.Arrays;
import org.junit.Test;

public class RequestErrorExceptionTest {
    
    @Test
    public void testRequestErrorException() {
        Assertions.assertThat(new RequestErrorException(new IllegalArgumentException()))
            .httpStatus(500)
            .code("INTERNAL_SERVER_ERROR");
    }

    @Test
    public void testRethrow() {
        assertThrows(() -> RequestErrorException.rethrow(new IllegalArgumentException()))
                .hasCauseExactlyInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    public void testComparator() {
        Assertions.assertThat(new RequestErrorException(Arrays.asList(
                notFound(), exception())))
            .httpStatus(500)
            .code("INTERNAL_SERVER_ERROR");

        Assertions.assertThat(new RequestErrorException(Arrays.asList(
                exception(), notFound())))
            .httpStatus(500)
            .code("INTERNAL_SERVER_ERROR");

    }
    
    @Test
    public void testComparatorEquals() {
        Assertions.assertThat(new RequestErrorException(Arrays.asList(
                notFound(), notFound())))
            .httpStatus(404)
            .code("NOT_FOUND");
    }

    private RequestError exception() {
        return RequestError.builder().status(StatusCode.INTERNAL_SERVER_ERROR).build();
    }

    private RequestError notFound() {
        return RequestError.builder().status(StatusCode.NOT_FOUND).build();
    }

}
