package com.github.restup.test;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import java.util.Map;
import org.hamcrest.Matcher;
import com.github.restup.test.matchers.ContentTypeMatcher;
import com.github.restup.test.resource.Contents;
import com.github.restup.test.resource.RelativeTestResource;

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

    public static class Builder {

        private final ApiExecutor executor;
        private ApiRequest.Builder request;
        private ApiResponse.Builder expected;
        private MediaType mediaType;

        private Matcher<Object> bodyMatcher;
        private int okStatus = 200;
        private boolean testNameAsMethodName;

        Builder(ApiExecutor executor, Class<?> unitTest, String path, Object... defaultPathArgs) {
            this.executor = executor;
            this.request = ApiRequest.builder(path, defaultPathArgs);
            this.request.testClass(unitTest);
            this.expected = new ApiResponse.Builder();
            this.expected.testClass(unitTest);
            this.testNameAsMethodName = true;
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

        public Builder method(HttpMethod method) {
            request.method(method);
            return me();
        }

        public Builder mediaType(MediaType mediaType) {
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
            return me();
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

        public Builder okStatus(int okStatus) {
            this.okStatus = okStatus;
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
                json(mediaType.getContentType());
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

            Object expectedValue = getContent(expectedBody, assertUsingString);
            Object responseValue = getContent(responseBody, assertUsingString);

            String message = "Body";

            boolean tddCheat = ContentsAssertions.tddCheat(expectedBody, responseBody);
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
         * @param limit page limit
         * @return this builder
         */
        public Builder page(int offset, int limit) {
            return param("offset", String.valueOf(offset)).param("limit", String.valueOf(limit));
        }

    }

}
