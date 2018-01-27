package com.github.restup.http.model;

import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class HttpServletResourceControllerResponseTest {

    @Mock
    HttpServletResponse httpServletResponse;
    
    @Test
    public void testResponse() {
        HttpServletResourceControllerResponse response = new HttpServletResourceControllerResponse(httpServletResponse);
        String name = "foo";
        String value = "bar";
        int status = 123;
        response.setHeader(name, value);
        response.setStatus(status);

        verify(httpServletResponse).setHeader(name, value);
        verify(httpServletResponse).setStatus(status);
    }
}
