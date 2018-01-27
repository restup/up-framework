package com.github.restup.test;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.hamcrest.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.restup.test.resource.Contents;
import com.github.restup.test.resource.RelativeTestResource;
import com.github.restup.test.resource.ResourceContents;
import com.github.restup.test.serializer.AutoDetectConstants;
import com.github.restup.test.serializer.GsonSerializer;
import com.github.restup.test.serializer.JacksonSerializer;
import com.github.restup.test.serializer.ResultSerializer;
import com.google.gson.Gson;

public class ContentsAssertions {

    private final static Logger log = LoggerFactory.getLogger(ContentsAssertions.class);

    public final static boolean tddCheat(Contents expected, Contents actual) {
        if (expected instanceof ResourceContents) {
            ResourceContents resource = (ResourceContents) expected;
            if (!resource.exists()) {
                resource.writeResult(actual.getContentAsByteArray());
                return true;
            }
        }
        return false;
    }

    public static Builder builder(Class<?> unitTest) {
        return new Builder(unitTest);
    }

    public static Builder json(Class<?> unitTest) {
        return new Builder(unitTest).json();
    }

    public static Builder builder() {
        return builder(RelativeTestResource.getClassFromStack());
    }

    public static Builder json() {
        return json(RelativeTestResource.getClassFromStack());
    }

    public static Builder json(ObjectMapper mapper) {
        return json().mapper(mapper);
    }

    public static Builder json(Gson gson) {
        return json().gson(gson);
    }

    public static class Builder {

        private Contents.Builder actual;
        private Contents.Builder expected;
        private Matcher<String> matcher;
        private ResultSerializer serializer;
        private boolean json;

        Builder(Class<?> unitTest) {
            this.actual = Contents.builder();
            this.actual.testClass(unitTest);
            this.expected = Contents.builder();
            this.expected.testClass(unitTest);
        }

        public Builder result(byte[] body) {
            actual.contents(body);
            return me();
        }

        public Builder result(String body) {
            actual.contents(body);
            return me();
        }

        public Builder result(Contents body) {
            actual.contents(body);
            return me();
        }

        public Builder gson(Gson gson) {
            return serializer(new GsonSerializer(gson));
        }

        public Builder mapper(ObjectMapper mapper) {
            return serializer(new JacksonSerializer(mapper));
        }

        public Builder serializer(ResultSerializer serializer) {
            this.serializer = serializer;
            return me();
        }

        public Builder result(Object value) {
            if ( serializer == null ) {
                if (AutoDetectConstants.JACKSON2_EXISTS) {
                    serializer = new JacksonSerializer();
                } else if (AutoDetectConstants.GSON_EXISTS) {
                    serializer = new GsonSerializer();
                } else {
                    throw new IllegalStateException("Unable to serialize value. Please add a supported serializer to classpath (Jackson, Gson))");
                }
            }
            String body = serializer.convertToString(value);

            actual.contents(body);
            return me();
        }

        Builder expect(byte[] body) {
            expected.contents(body);
            return me();
        }

        Builder expect(String body) {
            expected.contents(body);
            return me();
        }

        Builder expect(Contents body) {
            expected.contents(body);
            return me();
        }

        public Builder matcher(Matcher<String> matcher) {
            this.matcher = matcher;
            return me();
        }

        public Builder json() {
            return json(true);
        }

        public Builder json(boolean json) {
            this.json = json;
            return me();
        }

        protected Builder me() {
            return this;
        }

        public void matches(byte[] contents) {
            expect(contents).build();
        }

        public void matches(String contents) {
            expect(contents).build();
        }

        public void matches(Contents contents) {
            expect(contents).build();
        }

        public void test(String testName) {
            actual.testName(testName);
            expected.testName(testName);
            build();
        }

        void build() {
            Contents expectedContents = expected.build();
            Contents actualContents = actual.build();

            String expected = expectedContents.getContentAsString();
            String actual = actualContents.getContentAsString();
            if (matcher == null) {
                if (json) {
                    matcher = jsonEquals(expected);
                } else {
                    matcher = is(expected);
                }
            }
            String message = "Contents";
            if (ContentsAssertions.tddCheat(expectedContents, actualContents)) {
                message = "Result has been written for convenience. Verify correctness of results for future executions";
            }
            log.debug("Expected:\n{}", expected);
            log.debug("Actual:\n{}", actual);
            assertThat(message, actual, matcher);
        }
    }

}
