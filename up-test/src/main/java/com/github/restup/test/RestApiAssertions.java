package com.github.restup.test;

import static com.github.restup.test.matchers.LocationMatcher.matchesLocationPath;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import com.github.restup.test.resource.Contents;
import com.github.restup.test.resource.RelativeTestResource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Provides a {@link Builder} for RESTful apis with test methods for collection and item resource aligned with http verbs.
 */
public class RestApiAssertions {

    private RestApiAssertions() {
        super();
    }

    public static Builder builder(ApiExecutor executor, Class<?> unitTest, String path, Object... args) {
        return new Builder(executor, unitTest, path, args);
    }

    public static Builder builder(ApiExecutor executor, Object unitTest, String path, Object... args) {
        return new Builder(executor, unitTest, path, args);
    }

    public static Builder builder(ApiExecutor executor, String path, Object... args) {
        return new Builder(executor, path, args);
    }

    public static class Builder {

        private final ApiExecutor executor;
        private final Class<?> unitTest;
        private final String path;
        private final Object[] defaultArgs;
        private String location;
        private boolean itemResource;
        private HttpMethod method;
        private Object[] pathArgs;
        private String testName;
        private MediaType mediaType;
        private boolean https;
        private List<RpcApiAssertionsBuilderDecorator> rpcApiAssertionsBuilderDecorators = new ArrayList();
        private boolean createMissingResource;
        private boolean contentsAssertions;
        private Boolean expectLocationHeader;
        Builder(ApiExecutor executor, Class<?> unitTest, String path, Object... pathArgs) {
            this.executor = executor;
            this.unitTest = unitTest;
            this.path = path;
            defaultArgs = pathArgs;
            mediaType = MediaType.APPLICATION_JSON;
            createMissingResource = true;
            contentsAssertions = true;
            expectLocationHeader = true;
        }

        Builder(ApiExecutor executor, Object unitTest, String path, Object... pathArgs) {
            this(executor, unitTest.getClass(), path, pathArgs);
        }

        Builder(ApiExecutor executor, String path, Object... pathArgs) {
            this(executor, RelativeTestResource.getClassFromStack(), path, pathArgs);
        }

        private Builder me() {
            return this;
        }

        /**
         * Set the location used for the request.  Expected to be returned from a Location header of
         * a prior request and have the full url of the resource.
         */
        public Builder location(String location) {
            this.location = location;
            return me();
        }

        /**
         * Create missing expected result files if true.  Tests will still fail, however the result
         * will be saved to the expected file
         *
         * @param createMissingResource if true, create missing resource
         */
        public Builder createMissingResource(boolean createMissingResource) {
            this.createMissingResource = createMissingResource;
            return me();
        }

        public Builder contentsAssertions(boolean contentsAssertions) {
            this.contentsAssertions = contentsAssertions;
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

        public Builder decorate(RpcApiAssertionsBuilderDecorator... decorators) {
            for (RpcApiAssertionsBuilderDecorator decorator : decorators) {
                rpcApiAssertionsBuilderDecorators.add(decorator);
            }
            return me();
        }

        public Builder decorate(Collection<RpcApiAssertionsBuilderDecorator> decorators) {
            for (RpcApiAssertionsBuilderDecorator decorator : decorators) {
                rpcApiAssertionsBuilderDecorators.add(decorator);
            }
            return me();
        }

        private Builder pathArgs(Object[] pathArgs) {
            this.pathArgs = pathArgs;
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

        public RpcApiAssertions.Builder add(byte[] body, Object... pathArgs) {
            return add(pathArgs).body(body);
        }

        public RpcApiAssertions.Builder add(String body, Object... pathArgs) {
            return add(pathArgs).body(body);
        }

        public RpcApiAssertions.Builder add(Contents body, Object... pathArgs) {
            return add(pathArgs).body(body);
        }

        public Builder expectLocationHeader(boolean expectLocationHeader) {
            this.expectLocationHeader = expectLocationHeader;
            return me();
        }

        public RpcApiAssertions.Builder add(Object... pathArgs) {
            RpcApiAssertions.Builder rpc = collectionResource()
                .method(HttpMethod.POST)
                .pathArgs(pathArgs)
                .build();

            if (expectLocationHeader) {
                rpc = rpc.expectHeader("Location", matchesLocationPath(path));
            }
            return rpc;
        }

        public RpcApiAssertions.Builder list(Object... pathArgs) {
            return collectionResource()
                    .method(HttpMethod.GET)
                .pathArgs(pathArgs)
                    .build();
        }

        public RpcApiAssertions.Builder update(byte[] body, Object... pathArgs) {
            return update(pathArgs).body(body);
        }

        public RpcApiAssertions.Builder update(String body, Object... pathArgs) {
            return update(pathArgs).body(body);
        }

        public RpcApiAssertions.Builder update(Contents body, Object... pathArgs) {
            return update(pathArgs).body(body);
        }

        public RpcApiAssertions.Builder update(Object... pathArgs) {
            return itemResource()
                    .method(HttpMethod.PUT)
                .pathArgs(pathArgs)
                .build()
                .expectStatus(HttpStatus.CREATED);
        }

        public RpcApiAssertions.Builder patch(byte[] body, Object... pathArgs) {
            return patch(pathArgs).body(body);
        }

        public RpcApiAssertions.Builder patch(String body, Object... pathArgs) {
            return patch(pathArgs).body(body);
        }

        public RpcApiAssertions.Builder patch(Contents body, Object... pathArgs) {
            return patch(pathArgs).body(body);
        }

        public RpcApiAssertions.Builder patch(Object... pathArgs) {
            return itemResource()
                    .method(HttpMethod.PATCH)
                .pathArgs(pathArgs)
                    .build();
        }

        public RpcApiAssertions.Builder get(Object... pathArgs) {
            return itemResource()
                    .method(HttpMethod.GET)
                .pathArgs(pathArgs)
                    .build();
        }

        public RpcApiAssertions.Builder delete(Object... pathArgs) {
            return itemResource()
                    .method(HttpMethod.DELETE)
                .pathArgs(pathArgs)
                .build()
                .expectStatus(HttpStatus.NO_CONTENT);
        }

        public Builder https() {
            return https(true);
        }

        public Builder https(boolean https) {
            this.https = https;
            return me();
        }

        private RpcApiAssertions.Builder build() {

            String testPath = location;
            if (isEmpty(testPath)) {
                testPath = path;
                if (testPath.contains("}")) {
                    if (!itemResource) {
                        // collection resource can't end with id
                        testPath = path.substring(0, path.lastIndexOf('/'));
                    }
                } else if (itemResource) {
                    // item resource has to end with id
                    testPath += "/{id}";
                }
            }

            return new RpcApiAssertions.Builder(executor, unitTest, testPath, defaultArgs)
                .pathArgs(pathArgs)
                .method(method)
                .test(testName)
                .mediaType(mediaType)
                .decorate(rpcApiAssertionsBuilderDecorators)
                .https(https)
                .createMissingResource(createMissingResource)
                .contentsAssertions(contentsAssertions);
        }
    }
}
