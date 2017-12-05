package com.github.restup.controller.interceptor;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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
