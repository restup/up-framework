package com.github.restup.controller.interceptor;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RequestInterceptorChainTest {

    @Mock
    RequestInterceptor interceptor;
    @Mock
    ParsedResourceControllerRequest<?> request;


    @Test(expected = AssertionError.class)
    public void testNull() {
        new RequestInterceptorChain();
    }

    @Test
    public void testBefore() {
        new RequestInterceptorChain(interceptor).before(request);
        verify(interceptor, times(1)).before(request);
    }

    @Test
    public void testAfter() {
        new RequestInterceptorChain(interceptor).after(request);
        verify(interceptor, times(1)).after(request);
    }

}
