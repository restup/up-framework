package com.github.restup.controller.interceptor;

import static org.mockito.Mockito.verify;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import com.github.restup.controller.model.ParsedResourceControllerRequest;

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
        verify(interceptor).before(request);
    }

    @Test
    public void testAfter() {
        new RequestInterceptorChain(interceptor).after(request);
        verify(interceptor).after(request);
    }

}
