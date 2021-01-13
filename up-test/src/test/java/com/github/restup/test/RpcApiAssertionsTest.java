package com.github.restup.test;

import static com.github.restup.test.assertions.Assertions.assertPrivateConstructor;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.github.restup.test.resource.Contents;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RpcApiAssertionsTest {

    @Mock
    ApiExecutor executor;
    @Mock
    ApiResponse<String[]> response;
    @Mock
    Contents expectedContents;
    @Mock
    Contents resultContents;

    private ArgumentCaptor<ApiRequest> request() {
        return request(200, "application/json");
    }

    private ArgumentCaptor<ApiRequest> request(int status, String contentType) {
        ArgumentCaptor<ApiRequest> request = ArgumentCaptor.forClass(ApiRequest.class);

        when(executor.execute(request.capture())).thenReturn(response);
        when(response.getStatus()).thenReturn(status);
        when(response.getHeader("Content-Type")).thenReturn(new String[]{contentType});
        when(response.getBody()).thenReturn(resultContents);
        return request;
    }

    @Test
    public void testPrivateConstructor() {
        assertPrivateConstructor(RpcApiAssertions.class);
    }

    @Test
    public void testGet() {
        ArgumentCaptor<ApiRequest> requestCaptor = request();

        RpcApiAssertions.builder(executor, "/foo/some/{non}/{restful}/{restful}", 1, 2, 3)
            .expectBody(expectedContents)
            .ok();

        assertItemRequest(requestCaptor, HttpMethod.GET);
    }

    private void assertItemRequest(ArgumentCaptor<ApiRequest> requestCaptor, HttpMethod method) {
        assertItemRequest(requestCaptor, method, null, false, "application/json");
    }

    private void assertItemRequest(ArgumentCaptor<ApiRequest> requestCaptor, HttpMethod method, Contents contents, boolean https, String contentType) {
        assertRequest(requestCaptor, method, "/foo/some/1/2/3", contents, https, contentType);
    }


    private void assertRequest(ArgumentCaptor<ApiRequest> requestCaptor, HttpMethod method, String url, Contents contents, boolean https, String contentType) {
        ApiRequest request = requestCaptor.getValue();

        assertEquals(https, request.isHttps());
        assertEquals(contents, request.getBody());
        assertEquals(method, request.getMethod());
        assertEquals(url, request.getUrl());
    }

}
