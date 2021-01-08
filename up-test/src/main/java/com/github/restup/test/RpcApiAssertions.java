package com.github.restup.test;

import static com.github.restup.test.resource.RelativeTestResource.response;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.github.restup.test.matchers.ContentTypeMatcher;
import com.github.restup.test.resource.Contents;
import com.github.restup.test.resource.RelativeTestResource;
import java.util.Map;
import org.hamcrest.Matcher;

/**
 * Provides a {@link Builder} with some defaults and support for easy file comparisons
 */
public class RpcApiAssertions {

    private RpcApiAssertions() {
        super();
    }

    public static Builder builder(ApiExecutor executor, Class<?> unitTest, String path, Object... defaultPathArgs) {
        return new Builder(executor, unitTest, path, defaultPathArgs);
    }

    public static interface Decorator {

        Builder decorate(Builder b);
    }

    public static class Builder {

        private final ApiExecutor executor;
        private ApiRequest.Builder request;
        private ApiResponse.Builder expected;
        private String mediaType;

        private Matcher<Object> bodyMatcher;
        private int okStatus = 200;
        private boolean testNameAsMethodName;
        private boolean createMissingResource;

        Builder(ApiExecutor executor, Class<?> unitTest, String path, Object... defaultPathArgs) {
            this.executor = executor;
            request = ApiRequest.builder(path, defaultPathArgs);
            request.testClass(unitTest);
            expected = new ApiResponse.Builder();
            expected.testClass(unitTest);
            testNameAsMethodName = true;
            createMissingResource = true;
        }

        protected Builder me() {
            return this;
        }

        public Builder test(String testName) {
            request.testName(testName);
            expected.testName(testName);
            return me();
        }

        /**
         * Uses the calling method's name as the test name]
         *
         * @return this builder
         */
        public Builder test() {
            return test(RelativeTestResource.getCallingMethodName());
        }

        /**
         * If true, the test name will be detected and applied automatically based upon the calling method's name
         *
         * @param testNameAsMethodName if true use the calling methods name as the default test name.  true by default
         * @return thid builder
         */
        public Builder testNameAsMethodName(boolean testNameAsMethodName) {
            this.testNameAsMethodName = testNameAsMethodName;
            return me();
        }

        public Builder testFileExtension(String extension) {
            request.testFileExtension(extension);
            expected.testFileExtension(extension);
            return me();
        }

        public Builder requestHeader(String name, String... values) {
            request.header(name, values);
            return me();
        }

        public Builder https() {
            return https(true);
        }

        public Builder https(boolean https) {
            request.https(https);
            return me();
        }

        public Builder body(byte[] body) {
            request.body(body);
            return me();
        }

        public Builder body(String body) {
            request.body(body);
            return me();
        }

        public Builder body(Contents body) {
            request.body(body);
            return me();
        }

        public Builder get() {
            return method(HttpMethod.GET);
        }

        /**
         * sets http method to POST
         *
         * @return this builder
         */
        public Builder post() {
            return method(HttpMethod.POST);
        }

        /**
         * sets http method to POST and request body to passed value
         *
         * @param body of request
         * @return this builder
         */
        public Builder post(byte[] body) {
            return body(body).post();
        }

        /**
         * sets http method to POST and request body to passed value
         *
         * @param body of request
         * @return this builder
         */
        public Builder post(String body) {
            return body(body).post();
        }

        /**
         * sets http method to POST and request body to passed value
         *
         * @param body of request
         * @return this builder
         */
        public Builder post(Contents body) {
            return body(body).post();
        }

        /**
         * sets http method to PATCH
         *
         * @return this builder
         */
        public Builder patch() {
            return method(HttpMethod.PATCH);
        }

        /**
         * sets http method to PATCH and request body to passed value
         *
         * @param body of request
         * @return this builder
         */
        public Builder patch(byte[] body) {
            return body(body).patch();
        }

        /**
         * sets http method to PATCH and request body to passed value
         *
         * @param body of request
         * @return this builder
         */
        public Builder patch(String body) {
            return body(body).patch();
        }

        /**
         * sets http method to PATCH and request body to passed value
         *
         * @param body of request
         * @return this builder
         */
        public Builder patch(Contents body) {
            return body(body).patch();
        }

        /**
         * sets http method to PUT
         *
         * @return this builder
         */
        public Builder put() {
            return method(HttpMethod.PUT);
        }

        /**
         * sets http method to PUT and request body to passed value
         *
         * @param body of request
         * @return this builder
         */
        public Builder put(byte[] body) {
            return body(body).put();
        }

        /**
         * sets http method to PUT and request body to passed value
         *
         * @param body of request
         * @return this builder
         */
        public Builder put(String body) {
            return body(body).put();
        }

        /**
         * sets http method to PUT and request body to passed value
         *
         * @param body of request
         * @return this builder
         */
        public Builder put(Contents body) {
            return body(body).put();
        }

        /**
         * sets http method to DELETE
         *
         * @return this builder
         */
        public Builder delete() {
            return method(HttpMethod.DELETE);
        }

        public Builder method(HttpMethod method) {
            request.method(method);
            return me();
        }

        /**
         * sets mediaType to {@link MediaType#APPLICATION_JSON}
         *
         * @return this builder
         */
        public Builder json() {
            return mediaType(MediaType.APPLICATION_JSON);
        }

        public Builder mediaType(MediaType mediaType) {
            return mediaType(mediaType.getContentType());
        }

        /**
         * Allows for the plethora of existing media type enums to be passed in.  The result of toString must return the desired media type
         *
         * @param mediaType
         * @return this builder
         */
        public Builder mediaType(Object mediaType) {
            if (mediaType != null) {
                return mediaType(mediaType.toString());
            }
            return me();
        }

        public Builder mediaType(String mediaType) {
            this.mediaType = mediaType;
            return me();
        }

        public Builder pathArgs(Object... pathArgs) {
            request.pathArgs(pathArgs);
            return me();
        }

        public Builder query(String queryString) {
            request.query(queryString);
            return me();
        }

        public Builder param(String name, String... values) {
            request.param(name, values);
            return me();
        }

        public Builder bodyMatcher(Matcher<Object> bodyMatcher) {
            this.bodyMatcher = bodyMatcher;
            return me();
        }

        public Builder expectStatus(int httpStatus) {
            expected.status(httpStatus);
            return okStatus(httpStatus);
        }

        public Builder expectHeader(String name, Matcher<String[]> matcher) {
            expected.header(name, matcher);
            return me();
        }

        public Builder expectHeader(String name, String... values) {
            return expectHeader(name, is(values));
        }

        public Builder expectBody(byte[] body) {
            expected.body(body);
            return me();
        }

        public Builder expectBody(String body) {
            expected.body(body);
            return me();
        }

        public Builder expectBody(Contents body) {
            expected.body(body);
            return me();
        }

        /**
         * Expects the body to match the contents of file relative to this class with a name corresponding to the test method name
         *
         * @return
         */
        public Builder expectBody() {
            return expectBody(response());
        }

        /**
         * Overrides the default successful expected status
         *
         * @param okStatus
         * @return
         */
        public Builder okStatus(int okStatus) {
            this.okStatus = okStatus;
            return me();
        }

        /**
         * Create missing expected result files if true.  Tests will still fail, however the result will be saved to the expected file
         *
         * @param createMissingResource if true, create missing resource
         */
        public Builder createMissingResource(boolean createMissingResource) {
            this.createMissingResource = createMissingResource;
            return me();
        }

        public ApiResponse<String[]> error400() {
            return error(400);
        }

        public ApiResponse<String[]> error401() {
            return error(401);
        }

        public ApiResponse<String[]> error403() {
            return error(403);
        }

        public ApiResponse<String[]> error404() {
            return error(404);
        }

        public ApiResponse<String[]> error(int status) {
            return expectStatus(status).build();
        }

        public ApiResponse<String[]> ok() {
            return expectStatus(okStatus).build();
        }

        private Builder json(String contentType) {
            return requestHeader("Content-Type", contentType).requestHeader("Accept", contentType)
                .expectHeader("Content-Type", new ContentTypeMatcher(contentType)).testFileExtension("json");
        }

        public ApiResponse<String[]> build() {
            if (mediaType != null) {
                json(mediaType);
            }
            if (testNameAsMethodName && !expected.hasConfiguredBody()) {
                test();
            }
            ApiRequest request = this.request.build();
            ApiResponse<Matcher<String[]>> expected = this.expected.build();
            ApiResponse<String[]> response = executor.execute(request);

            // assert status
            assertThat("Status ", response.getStatus(), is(expected.getStatus()));

            // header assertions
            for (Map.Entry<String, Matcher<String[]>> e : expected.getHeaders().entrySet()) {
                assertThat(e.getKey() + " Header", response.getHeader(e.getKey()), e.getValue());
            }

            Contents expectedBody = expected.getBody();
            Contents responseBody = response.getBody();

            // Convert to String for assertions so that assertion
            // failure messages are meaningful
            boolean assertUsingString = isAssertByteAsString(response);

            boolean tddCheat =
                createMissingResource && ContentsAssertions.tddCheat(expectedBody, responseBody);
            Object expectedValue = tddCheat ? null : getContent(expectedBody, assertUsingString);
            Object responseValue = getContent(responseBody, assertUsingString);

            String message = "Body";

            if (tddCheat) {
                message = "Result has been written for convenience. Verify correctness of results for future executions";
            }

            if (bodyMatcher == null & (tddCheat || expectedValue != null)) {
                if (isJson(response)) {
                    bodyMatcher = jsonEquals(expectedValue);
                } else {
                    bodyMatcher = is(expectedValue);
                }
            }

            // compare body
            if (bodyMatcher != null) {
                assertThat(message, responseValue, bodyMatcher);
            }
            return response;
        }

        private Object getContent(Contents body, boolean assertUsingString) {
            if (body == null) {
                return null;
            }
            return assertUsingString ? body.getContentAsString() : body.getContentAsByteArray();
        }

        private boolean isJson(ApiResponse<String[]> response) {
            return headerContains(response, "Content-Type", "json");
        }

        private boolean isAssertByteAsString(ApiResponse<String[]> response) {
            return headerContains(response, "Content-Type", "json", "xml", "text");
        }

        private boolean headerContains(ApiResponse<String[]> response, String header, String... values) {
            String[] headers = response.getHeader(header);
            if (headers != null) {
                for (String s : headers) {
                    for (String value : values) {
                        if (s.contains(value)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        /**
         * Equivalent to
         * <pre>
         * param("offset", String.valueOf(offset)).param("limit", String.valueOf(limit))
         * </pre>
         *
         * @param offset page offset
         * @param limit  page limit
         * @return this builder
         */
        public Builder page(int offset, int limit) {
            return param("offset", String.valueOf(offset)).param("limit", String.valueOf(limit));
        }

    }

}
