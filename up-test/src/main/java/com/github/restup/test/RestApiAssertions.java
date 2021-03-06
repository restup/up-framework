package com.github.restup.test;

import static com.github.restup.test.resource.RelativeTestResource.getClassFromStack;

import com.github.restup.test.RpcApiAssertions.Decorator;
import com.github.restup.test.resource.Contents;
import org.apache.commons.lang3.StringUtils;

/**
 * Provides a {@link Builder} for RESTful apis with test methods for collection and item resource aligned with http verbs.
 */
public class RestApiAssertions {

    private RestApiAssertions() {
        super();
    }

    /**
     * Create a new {@link Builder}
     *
     * @param executor to execute the api request
     * @param unitTest specifies an instance which test resources will be relative to
     * @param path     of request
     * @param args     path bind args
     * @return a new {@link Builder}
     */
    public static Builder builder(ApiExecutor executor, Object unitTest, String path, Object... args) {
        return builder(executor, unitTest.getClass(), path, args);
    }

    /**
     * Create a new {@link Builder}
     *
     * @param executor to execute the api request
     * @param unitTest specifies the class which test resources will be relative to
     * @param path     of request
     * @param args     path bind args
     * @return a new {@link Builder}
     */
    public static Builder builder(ApiExecutor executor, Class<?> unitTest, String path, Object... args) {
        return new Builder(executor, unitTest, path, args);
    }

    /**
     * Create a new {@link Builder} using class invoking this method for relative test resources
     *
     * @param executor to execute the api request
     * @param path     of request
     * @param args     path bind args
     * @return a new {@link Builder}
     */
    public static Builder builder(ApiExecutor executor, String path, Object... args) {
        return builder(executor, getClassFromStack(), path, args);
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
        private String testName;
        private MediaType mediaType;
        private boolean https;
        private Decorator decorator;
        private boolean createMissingResource;

        Builder(ApiExecutor executor, Class<?> unitTest, String path, Object... args) {
            this.executor = executor;
            this.unitTest = unitTest;
            this.path = path;
            defaultArgs = args;
            mediaType = MediaType.APPLICATION_JSON;
            decorator = (b) -> b;
            createMissingResource = true;
        }

        static boolean isResourceIdParameterized(String testPath) {
            int i = testPath.lastIndexOf('/');
            i = testPath.indexOf("}", i);
            return i >= 0;
        }

        private Builder me() {
            return this;
        }

        /**
         * Create missing expected result files if true.  Tests will still fail, however the result will be saved to the expected file
         *
         * @param createMissingResource if true, create missing resource
         * @return this builder
         */
        public Builder createMissingResource(boolean createMissingResource) {
            this.createMissingResource = createMissingResource;
            return me();
        }

        private Builder method(HttpMethod method) {
            this.method = method;
            return me();
        }

        public Builder jsonapi() {
            return mediaType(MediaType.APPLICATION_JSON_API);
        }

        public Builder hal() {
            return mediaType(MediaType.APPLICATION_JSON_HAL);
        }

        public Builder mediaType(MediaType mediaType) {
            this.mediaType = mediaType;
            return me();
        }

        public Builder decorator(Decorator decorator) {
            this.decorator = decorator;
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
            itemResource = item;
            return me();
        }

        /**
         * @param testName used as default relative resource file names
         * @return this builder
         */
        public Builder test(String testName) {
            this.testName = testName;
            return me();
        }

        public RpcApiAssertions.Builder add(byte[] body, Object... args) {
            return add(args).body(body);
        }

        public RpcApiAssertions.Builder add(String body, Object... args) {
            return add(args).body(body);
        }

        public RpcApiAssertions.Builder add(Contents body, Object... args) {
            return add(args).body(body);
        }

        public RpcApiAssertions.Builder add(Object... args) {
            okStatus = 201;
            return collectionResource()
                .method(HttpMethod.POST)
                .args(args)
                .build();
        }

        public RpcApiAssertions.Builder list(Object... args) {
            return collectionResource()
                .method(HttpMethod.GET)
                .args(args)
                .build();
        }

        public RpcApiAssertions.Builder update(byte[] body, Object... args) {
            return update(args).body(body);
        }

        public RpcApiAssertions.Builder update(String body, Object... args) {
            return update(args).body(body);
        }

        public RpcApiAssertions.Builder update(Contents body, Object... args) {
            return update(args).body(body);
        }

        public RpcApiAssertions.Builder update(Object... args) {
            return itemResource()
                .method(HttpMethod.PUT)
                .args(args)
                .build();
        }

        public RpcApiAssertions.Builder patch(byte[] body, Object... args) {
            return patch(args).body(body);
        }

        public RpcApiAssertions.Builder patch(String body, Object... args) {
            return patch(args).body(body);
        }

        public RpcApiAssertions.Builder patch(Contents body, Object... args) {
            return patch(args).body(body);
        }

        public RpcApiAssertions.Builder patch(Object... args) {
            return itemResource()
                .method(HttpMethod.PATCH)
                .args(args)
                .build();
        }

        public RpcApiAssertions.Builder get(Object... args) {
            return itemResource()
                .method(HttpMethod.GET)
                .args(args)
                .build();
        }

        public RpcApiAssertions.Builder delete(Object... args) {
            return itemResource()
                .method(HttpMethod.DELETE)
                .args(args)
                .build();
        }

        public Builder https() {
            return https(true);
        }

        public Builder https(boolean https) {
            this.https = https;
            return me();
        }

        private RpcApiAssertions.Builder build() {

            String testPath = path;
            if (isResourceIdParameterized(testPath)) {
                if (!itemResource) {
                    // collection resource can't end with id
                    testPath = path.substring(0, path.lastIndexOf('/'));
                }
            } else if (itemResource) {

                int tokens = StringUtils.countMatches(testPath, "}");
                if (tokens + 1 == defaultArgs.length) {
                    // item resource has to end with id
                    testPath += "/{id}";
                }
            }

            return decorator
                .decorate(new RpcApiAssertions.Builder(executor, unitTest, testPath, defaultArgs)
                    .pathArgs(args)
                    .method(method)
                    .test(testName)
                    .mediaType(mediaType)
                    .https(https)
                    .createMissingResource(createMissingResource)
                    .okStatus(okStatus));
        }

    }
}
