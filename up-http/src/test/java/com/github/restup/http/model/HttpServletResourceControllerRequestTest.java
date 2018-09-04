package com.github.restup.http.model;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.github.restup.controller.content.negotiation.ContentTypeNegotiation;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.registry.settings.RegistrySettings;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HttpServletResourceControllerRequestTest {

    @Mock
    HttpServletRequest httpRequest;

    @Mock
    ResourceRegistry registry;

    @Test
    public void testGetContentTypeHeader() {
        String mediaType = "application/json";
        verifyContentType(mediaType, Collections.enumeration(Arrays.asList(mediaType+"; charset=utf-8")));
    }

    @Test
    public void testGetContentTypeHeaderNull() {
        verifyContentType(null, Collections.emptyEnumeration());
    }
    
    private void verifyContentType(String expected, Enumeration<String> headers) {
        when(httpRequest.getHeaders(ContentTypeNegotiation.CONTENT_TYPE)).thenReturn(headers);
        assertEquals(expected, HttpServletResourceControllerRequest.getContentType(httpRequest));

        verify(httpRequest).getHeaders(ContentTypeNegotiation.CONTENT_TYPE);
        verifyNoMoreInteractions(httpRequest);
    }

    @Test
    public void testRequest() {
        when(httpRequest.getRequestURL()).thenReturn(new StringBuffer("http://www.foo.com/"));
        when(httpRequest.getRequestURI()).thenReturn("/");
        when(httpRequest.getParameterNames()).thenReturn(mock(Enumeration.class));
        when(registry.getSettings()).thenReturn(mock(RegistrySettings.class));

        HttpServletResourceControllerRequest request = HttpServletResourceControllerRequest.builder(httpRequest)
            .registry(registry)
                .build();

        request.getParameterNames();
        request.getParameter("foo");
        request.getHeaders("bar");
        request.getHeaders("Referrer");
        verify(httpRequest).getRequestURL();
        verify(httpRequest).getRequestURI();
        verify(httpRequest).getMethod();
        verify(httpRequest).getHeaders("Content-Type");
        verify(httpRequest).getParameterNames();
        verify(httpRequest).getParameterValues("foo");
        verify(httpRequest).getHeaders("bar");
        verify(httpRequest).getHeaders("Referrer");
        verifyNoMoreInteractions(httpRequest);
    }
    
    @Test
    public void testBuilder() {
        // bridge function creates a "me()" method because of generics.
        // this provides cobertura coverage for me()
        HttpServletResourceControllerRequest.Builder builder =
        new HttpServletResourceControllerRequest.Builder(httpRequest);
        assertEquals(builder, builder.me());
    }


}
