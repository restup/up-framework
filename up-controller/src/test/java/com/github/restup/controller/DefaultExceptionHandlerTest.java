package com.github.restup.controller;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import org.junit.Test;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerResponse;
import com.github.restup.errors.RequestErrorException;

public class DefaultExceptionHandlerTest {

    private DefaultExceptionHandler handler = DefaultExceptionHandler.getInstance();

    @Test
    public void testHandle500() {
        RequestErrorException e = RequestErrorException.of(new IllegalArgumentException());
        ResourceControllerRequest request = mock(ResourceControllerRequest.class);
        ResourceControllerResponse response = mock(ResourceControllerResponse.class);
        RequestErrorException result = (RequestErrorException) handler.handleException(request, response, e);
        assertSame(e, result);
    }

    @Test
    public void testHandleException() {
        IllegalArgumentException e = new IllegalArgumentException();
        ResourceControllerRequest request = mock(ResourceControllerRequest.class);
        ResourceControllerResponse response = mock(ResourceControllerResponse.class);
        RequestErrorException result = (RequestErrorException) handler.handleException(request, response, e);
        assertSame(e, result.getCause());
    }

}
