package com.github.restup.test;

import static com.github.restup.test.assertions.Assertions.assertPrivateConstructor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.github.restup.test.assertions.Assertions;
import com.github.restup.test.resource.Contents;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RestApiAssertionsTest {

    @Mock
    ApiExecutor executor;
    @Mock
    ApiResponse<String[]> response;
    @Mock
    Contents expectedContents;
    @Mock
    Contents resultContents;

    static void assertHeaders(ApiRequest request, String mediaType) {
        assertEquals(2, request.getHeaders().size());
        String[] header = {mediaType};
        assertThat(request.getHeaders().get("Content-Type"), is(header));
        assertThat(request.getHeaders().get("Accept"), is(header));
    }

    @Test
    public void testPrivateConstructor() {
        assertPrivateConstructor(RestApiAssertions.class);
    }

    private Contents body(String contents) {
        when(expectedContents.getContentAsString()).thenReturn(contents);
        when(resultContents.getContentAsString()).thenReturn(contents);
        return resultContents;
    }

    private RestApiAssertions.Builder api() {
        return api("/foo");
    }

    private RestApiAssertions.Builder api(String endPoint) {
        return RestApiAssertions.builder(executor, endPoint, 1);
    }

    private Contents body() {
        return body("{\"name\":\"foo\"}");
    }

    private ArgumentCaptor<ApiRequest> request() {
        return request(200, "application/json");
    }

    private ArgumentCaptor<ApiRequest> request(int status, String contentType) {
        ArgumentCaptor<ApiRequest> request = ArgumentCaptor.forClass(ApiRequest.class);

        when(executor.execute(request.capture())).thenReturn(response);
        when(response.getStatus()).thenReturn(status);
        when(response.getHeader("Content-Type")).thenReturn(new String[] {contentType});
        when(response.getHeader("Location")).thenReturn(new String[]{"http://localhost/foo/111"});
        when(response.getBody()).thenReturn(resultContents);
        return request;
    }

    @Test
    public void testPatchOK() {
        ArgumentCaptor<ApiRequest> requestCaptor = request();

        Contents body = body();

        api().patch(body).expectBody(expectedContents).ok();

        assertItemRequest(requestCaptor, HttpMethod.PATCH, body);
    }

    @Test
    public void testUpdateOK() {
        ArgumentCaptor<ApiRequest> requestCaptor = request();

        Contents body = body();

        api("foo/{id}").update(body).expectBody(expectedContents).ok();

        assertItemRequest(requestCaptor, HttpMethod.PUT, body);
    }

    @Test
    public void testTestFileNotFound() {
        request();
        assertThrows(".json not found", () -> api().createMissingResource(false).update().ok());
    }

    @Test
    public void testTestFileNotFoundTestName() {
        request();
        assertThrows("foo.json not found",
            () -> api().test("foo").createMissingResource(false).update().ok());
    }

    @Test
    public void testContentTypeHalError() {
        request();

        Contents body = body();

        assertThrows("Expected: Content-Type=", () -> api("foo/{id}").hal()
            .patch(body.getContentAsByteArray()).contentsAssertions(false).ok());
    }

    @Test
    public void testBodyError() {
        request();
        assertThrows("Expected: foo", () -> api()
                .patch(body().getContentAsString())
                .expectBody("foo").ok());

        assertThrows("Expected: foo", () -> api()
                .update(body().getContentAsString())
                .expectBody("foo").ok());

        assertThrows("Expected: foo", () -> api()
                .update(body().getContentAsByteArray())
                .expectBody("foo").ok());
    }

    @Test
    public void testAddOK() {
        String contentType = MediaType.APPLICATION_JSON_API.getContentType();
        ArgumentCaptor<ApiRequest> requestCaptor = request(201, contentType);

        Contents body = body();

        api().jsonapi().https().add(body)
            .expectBody(expectedContents).created();

        assertCollectionRequest(requestCaptor, HttpMethod.POST, body, true, contentType);
    }

    @Test
    public void testContentTypeError() {
        request(201, MediaType.APPLICATION_JSON_API.getContentType());

        Contents body = body();

        assertThrows("Expected: Content-Type=", () -> api()
                .add(body.getContentAsString())
            .expectBody(expectedContents).created());
    }

    @Test
    public void testStatusError() {
        request();

        assertThrows("Expected: is <201>", () -> api()
                .add(body().getContentAsByteArray())
            .expectBody(expectedContents).created());
    }

    private void assertThrows(String messageContains, ThrowingCallable f) {
        Assertions.assertThrows(f, AssertionError.class).hasMessageContaining(messageContains);
    }

    @Test
    public void testGetOK() {
        ArgumentCaptor<ApiRequest> requestCaptor = request();

        api().get().expectBody(expectedContents).ok();

        assertItemRequest(requestCaptor, HttpMethod.GET);
    }

    @Test
    public void testDeleteOK() {
        ArgumentCaptor<ApiRequest> requestCaptor = request();

        RestApiAssertions.builder(executor, this, "/foo", 1)
                .delete()
                .expectBody(expectedContents).ok();

        assertItemRequest(requestCaptor, HttpMethod.DELETE);
    }

    @Test
    public void testListOK() {
        ArgumentCaptor<ApiRequest> requestCaptor = request();

        RestApiAssertions.builder(executor, getClass(), "/foo/{id}", 1)
                .mediaType(MediaType.APPLICATION_JSON)
                .list()
                .expectBody(expectedContents).ok();

        assertCollectionRequest(requestCaptor, HttpMethod.GET);
    }

    private void assertItemRequest(ArgumentCaptor<ApiRequest> requestCaptor, HttpMethod method) {
        assertItemRequest(requestCaptor, method, false);
    }

    private void assertItemRequest(ArgumentCaptor<ApiRequest> requestCaptor, HttpMethod method, Contents contents) {
        assertItemRequest(requestCaptor, method, contents, false, "application/json");
    }

    private void assertItemRequest(ArgumentCaptor<ApiRequest> requestCaptor, HttpMethod method, boolean https) {
        assertItemRequest(requestCaptor, method, null, https, "application/json");
    }

    private void assertItemRequest(ArgumentCaptor<ApiRequest> requestCaptor, HttpMethod method, Contents contents, boolean https, String contentType) {
        assertRequest(requestCaptor, method, "/foo/1", contents, https, contentType);
    }

    private void assertCollectionRequest(ArgumentCaptor<ApiRequest> requestCaptor, HttpMethod method) {
        assertCollectionRequest(requestCaptor, method, false);
    }

    private void assertCollectionRequest(ArgumentCaptor<ApiRequest> requestCaptor, HttpMethod method, Contents contents) {
        assertCollectionRequest(requestCaptor, method, contents, false, "application/json");
    }

    private void assertCollectionRequest(ArgumentCaptor<ApiRequest> requestCaptor, HttpMethod method, boolean https) {
        assertCollectionRequest(requestCaptor, method, null, https, "application/json");
    }

    private void assertCollectionRequest(ArgumentCaptor<ApiRequest> requestCaptor, HttpMethod method, Contents contents, boolean https, String contentType) {
        assertRequest(requestCaptor, method, "/foo", contents, https, contentType);
    }

    private void assertRequest(ArgumentCaptor<ApiRequest> requestCaptor, HttpMethod method, String url, Contents contents, boolean https, String contentType) {
        ApiRequest request = requestCaptor.getValue();

        assertEquals(https, request.isHttps());
        assertEquals(contents, request.getBody());
        assertHeaders(request, contentType);
        assertEquals(method, request.getMethod());
        assertEquals(url, request.getUrl());
    }
}
