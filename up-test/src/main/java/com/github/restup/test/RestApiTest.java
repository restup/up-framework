package com.github.restup.test;

import com.github.restup.test.resource.Contents;
import com.github.restup.test.resource.RelativeTestResource;

import static com.github.restup.test.RpcApiTest.HttpMethod;

/**
 * Provides a {@link Builder} for RESTful apis with test methods
 * for collection and item resource aligned with http verbs.
 */
public class RestApiTest {

    private RestApiTest() {

    }

    public static class Builder {

        private final ApiExecutor executor;
        private final Class<?> unitTest;
        private final String path;
        private final Object[] defaultArgs;
        private boolean itemResource;
        private HttpMethod method;
        private Object[] args;
        private int okStatus = 200;
        private boolean testNameAsMethodName;
        private RpcApiTest.MediaType mediaType;

        public Builder(ApiExecutor executor, Class<?> unitTest, String path, Object... args) {
            this.executor = executor;
            this.unitTest = unitTest;
            this.path = path;
            this.defaultArgs = args;
            this.testNameAsMethodName = true;
            mediaType = RpcApiTest.MediaType.APPLICATION_JSON;
        }

        public Builder(ApiExecutor executor, Object unitTest, String path, Object... args) {
            this(executor, unitTest.getClass(), path, args);
        }

        public Builder(ApiExecutor executor, String path, Object... args) {
            this(executor, RelativeTestResource.getClassFromStack(), path, args);
        }

        private Builder me() {
            return this;
        }

        private Builder method(HttpMethod method) {
            this.method = method;
            return me();
        }


        public Builder jsonapi() {
            return mediaType(RpcApiTest.MediaType.APPLICATION_JSON_API);
        }

        public Builder hal() {
            return mediaType(RpcApiTest.MediaType.APPLICATION_JSON_HAL);
        }

        public Builder mediaType(RpcApiTest.MediaType mediaType) {
            this.mediaType = mediaType;
            return me();
        }

        private Builder args(Object[] args) {
            this.args = args;
            return me();
        }

        private Builder collectionResource() {
            return itemResource(false);
        }

        private Builder itemResource() {
            return itemResource(true);
        }

        private Builder itemResource(boolean item) {
            this.itemResource = item;
            return me();
        }

        /**
         * If true, the test name will be detected and applied automatically based upon the
         * calling method's name
         *
         * @param testNameAsMethodName
         * @return
         */
        public Builder testNameAsMethodName(boolean testNameAsMethodName) {
            this.testNameAsMethodName = testNameAsMethodName;
            return me();
        }

        public RpcApiTest.Builder add(byte[] body, Object... args) {
            return add(args).body(body);
        }

        public RpcApiTest.Builder add(String body, Object... args) {
            return add(args).body(body);
        }

        public RpcApiTest.Builder add(Contents body, Object... args) {
            return add(args).body(body);
        }

        public RpcApiTest.Builder add(Object... args) {
            okStatus = 201;
            return collectionResource()
                    .method(HttpMethod.POST)
                    .args(args)
                    .build();
        }

        public RpcApiTest.Builder list(Object... args) {
            return collectionResource()
                    .method(HttpMethod.GET)
                    .args(args)
                    .build();
        }

        public RpcApiTest.Builder update(byte[] body, Object... args) {
            return update(args).body(body);
        }

        public RpcApiTest.Builder update(String body, Object... args) {
            return update(args).body(body);
        }

        public RpcApiTest.Builder update(Contents body, Object... args) {
            return update(args).body(body);
        }

        public RpcApiTest.Builder update(Object... args) {
            return itemResource()
                    .method(HttpMethod.PUT)
                    .args(args)
                    .build();
        }

        public RpcApiTest.Builder patch(byte[] body, Object... args) {
            return patch(args).body(body);
        }

        public RpcApiTest.Builder patch(String body, Object... args) {
            return patch(args).body(body);
        }

        public RpcApiTest.Builder patch(Contents body, Object... args) {
            return patch(args).body(body);
        }

        public RpcApiTest.Builder patch(Object... args) {
            return itemResource()
                    .method(HttpMethod.PATCH)
                    .args(args)
                    .build();
        }

        public RpcApiTest.Builder get(Object... args) {
            return itemResource()
                    .method(HttpMethod.GET)
                    .args(args)
                    .build();
        }

        public RpcApiTest.Builder delete(Object... args) {
            return itemResource()
                    .method(HttpMethod.DELETE)
                    .args(args)
                    .build();
        }

        private RpcApiTest.Builder build() {


            String testPath = path;
            if (testPath.contains("}")) {
                if (!itemResource) {
                    // collection resource can't end with id
                    testPath = path.substring(0, path.lastIndexOf('/'));
                }
            } else if (itemResource) {
                // item resource has to end with id
                testPath += "/{id}";
            }

            return new RpcApiTest.Builder(executor, unitTest, testPath, defaultArgs)
                    .pathArgs(args)
                    .method(method)
                    .testNameAsMethodName(testNameAsMethodName)
                    .mediaType(mediaType)
                    .okStatus(okStatus);
        }
    }
}
