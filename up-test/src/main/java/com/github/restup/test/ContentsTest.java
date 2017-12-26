package com.github.restup.test;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.restup.test.resource.ByteArrayContents;
import com.github.restup.test.resource.Contents;
import com.github.restup.test.resource.RelativeTestResource;
import com.github.restup.test.resource.ResourceContents;
import com.github.restup.test.resource.StringContents;
import com.github.restup.test.serializer.AutoDetectConstants;
import com.github.restup.test.serializer.GsonSerializer;
import com.github.restup.test.serializer.JacksonSerializer;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentsTest {

    private final static Logger log = LoggerFactory.getLogger(ContentsTest.class);

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

        private ContentsBuilder actual;
        private ContentsBuilder expected;
        private Matcher<String> matcher;
        private boolean json;
        private Gson gson;
        private ObjectMapper mapper;

        Builder(Class<?> unitTest) {
            this.actual = new ContentsBuilder();
            this.actual.testClass(unitTest);
            this.expected = new ContentsBuilder();
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
            this.gson = gson;
            return me();
        }

        public Builder mapper(ObjectMapper mapper) {
            this.mapper = mapper;
            return me();
        }

        public Builder result(Object value) {
            String body = null;
            if (json) {
                if (AutoDetectConstants.JACKSON2_EXISTS) {
                    body = JacksonSerializer.convertToString(mapper, value);
                } else if (AutoDetectConstants.GSON_EXISTS) {
                    body = GsonSerializer.convertToString(gson, value);
                }
            }
            //TODO xml
            if (body == null) {
                throw new IllegalStateException("Unable to serialize value. Please add a supported serializer to classpath (Jackson, Gson))");
            }
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
            if (ContentsTest.tddCheat(expectedContents, actualContents)) {
                message = "Result has been written for convenience. Verify correctness of results for future executions";
            }
            log.debug("Expected:\n{}", expected);
            log.debug("Actual:\n{}", actual);
            assertThat(message, actual, matcher);
        }
    }

    private static class ContentsBuilder {

        private Class<?> testClass;
        private String testName;
        private Contents contents;

        protected ContentsBuilder me() {
            return this;
        }

        public ContentsBuilder contents(byte[] body) {
            return contents(new ByteArrayContents(body));
        }

        public ContentsBuilder contents(String body) {
            return contents(new StringContents(body));
        }

        public ContentsBuilder contents(Contents bodyResource) {
            this.contents = bodyResource;
            return me();
        }

        public ContentsBuilder testClass(Class<?> testClass) {
            this.testClass = testClass;
            return me();
        }

        public ContentsBuilder testName(String testName) {
            this.testName = testName;
            return me();
        }

        public Contents build() {
            Contents result = this.contents;
            if (result == null && StringUtils.isNotBlank(testName)) {
                result = new RelativeTestResource(testClass, testName);
            }
            return result;
        }
    }

}
