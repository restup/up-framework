package com.github.restup.test;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.restup.test.resource.Contents;
import com.github.restup.test.resource.RelativeTestResource;
import com.github.restup.test.resource.ResourceContents;
import com.github.restup.test.serializer.AutoDetectConstants;
import com.github.restup.test.serializer.GsonSerializer;
import com.github.restup.test.serializer.JacksonSerializer;
import com.github.restup.test.serializer.ResultSerializer;
import com.google.gson.Gson;
import org.hamcrest.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a {@link Builder} to build and compare expected and actual result {@link Contents}
 * 
 * @author abuttaro
 *
 */
public class ContentsAssertions {

    private final static Logger log = LoggerFactory.getLogger(ContentsAssertions.class);

    private ContentsAssertions() {
        super();
    }

    public final static boolean tddCheat(Contents expected, Contents actual) {
        if (expected instanceof ResourceContents) {
            ResourceContents resource = (ResourceContents) expected;
            if (actual != null && !resource.exists()) {
                resource.writeResult(actual.getContentAsByteArray());
                return true;
            }
        }
        return false;
    }


    /**
     * Creates a {@link Builder} to assert contents using the calling class for relative path {@link
     * Contents}
     *
     * @return a new contents assertions builder
     */
    public static Builder builder() {
        return ContentsAssertions.builder(RelativeTestResource.getClassFromStack());
    }

    /**
     * Creates a {@link Builder} to assert contents using a specific class for relative path {@link
     * Contents}
     *
     * @param unitTest to use for relative resources
     * @return a new contents assertions builder
     */
    public static Builder builder(Class<?> unitTest) {
        return new Builder(unitTest);
    }

    /**
     * An alias for {@link #builder()} for fluently asserting a assertText match
     *
     * @return a new contents assertions builder
     */
    public static Builder assertText() {
        return ContentsAssertions.builder();
    }

    /**
     * An alias for {@link #builder(Class)} for fluently asserting a assertText match
     *
     * @param unitTest to use for relative resources
     * @return a new contents assertions builder
     */
    public static Builder assertText(Class<?> unitTest) {
        return ContentsAssertions.builder(unitTest);
    }

    /**
     * A convenience method to create a {@link Builder} to assert assertJson contents using a
     * specific class for any relative path {@link Contents}
     *
     * @param unitTest to use for relative resources
     * @return a new contents assertions builder
     */
    public static Builder assertJson(Class<?> unitTest) {
        return new Builder(unitTest).json();
    }

    /**
     * A convenience method to create a {@link Builder} to assert assertJson contents using the
     * calling class for relative path {@link Contents}
     *
     * @return a new contents assertions builder
     */
    public static Builder assertJson() {
        return ContentsAssertions.assertJson(RelativeTestResource.getClassFromStack());
    }

    /**
     * A convenience method to create a {@link Builder} to assert assertJson contents using a
     * specific Jackson {@link ObjectMapper} instance
     *
     * @param mapper to use for json serialization
     * @return a new contents assertions builder
     */
    public static Builder assertJson(ObjectMapper mapper) {
        return ContentsAssertions.assertJson().mapper(mapper);
    }

    /**
     * A convenience method to create a {@link Builder} to assert assertJson contents using a
     * specific {@link Gson} instance
     *
     * @param gson to use for json serialization
     * @return a new contents assertions builder
     */
    public static Builder assertJson(Gson gson) {
        return ContentsAssertions.assertJson().gson(gson);
    }

    public static class Builder {

        private final Contents.Builder actual;
        private final Contents.Builder expected;
        private Matcher<String> matcher;
        private ResultSerializer serializer;
        private boolean json;

        Builder(Class<?> unitTest) {
            actual = Contents.builder();
            actual.relativeTo(unitTest);
            expected = Contents.builder();
            expected.relativeTo(unitTest);
        }

        Builder result(byte[] body) {
            actual.contents(body);
            return me();
        }

        Builder result(String body) {
            actual.contents(body);
            return me();
        }

        Builder result(Contents body) {
            actual.contents(body);
            return me();
        }

        Builder result(Object value) {
            String body = serialize(value);
            return result(body);
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

        public String serialize(Object value) {
            if (serializer == null) {
                if (AutoDetectConstants.JACKSON2_EXISTS) {
                    serializer = new JacksonSerializer();
                } else if (AutoDetectConstants.GSON_EXISTS) {
                    serializer = new GsonSerializer();
                } else {
                    throw new IllegalStateException("Unable to serialize value. Please add a supported serializer to classpath (Jackson, Gson))");
                }
            }
            return serializer.convertToString(value);
        }

        public Builder expect(byte[] body) {
            expected.contents(body);
            return me();
        }

        public Builder expect(String body) {
            expected.contents(body);
            return me();
        }

        public Builder expect(Contents body) {
            expected.contents(body);
            return me();
        }

        public Builder expect(Object body) {
            return expect(serialize(body));
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
            String type = json ? "json" : null;
            expected.type(type);
            actual.type(type);
            return me();
        }

        protected Builder me() {
            return this;
        }

        /**
         * assert expected matches the provided contents
         * 
         * @param contents to match
         */
        public void matches(byte[] contents) {
            result(contents).build();
        }

        /**
         * assert expected matches the provided contents
         * 
         * @param contents to match
         */
        public void matches(String contents) {
            result(contents).build();
        }

        /**
         * assert expected matches the provided contents
         * 
         * @param contents to match
         */
        public void matches(Contents contents) {
            result(contents).build();
        }

        /**
         * assert expected matches the provided contents
         * 
         * @param contents to match
         */
        public void matches(Object contents) {
            result(contents).build();
        }

        /**
         * 
         * @param testName name of test to use.  Used by default for relative resource file names.
         * @return builder this builder
         */
        public Builder test(String testName) {
            actual.name(testName);
            expected.name(testName);
            return me();
        }

        void build() {
            Contents expectedContents = expected.build();
            Contents actualContents = actual.build();


            String expected = null;
            String message = "Contents";
            if (ContentsAssertions.tddCheat(expectedContents, actualContents)) {
                message = "Result has been written for convenience. Verify correctness of results for future executions";
            } else {
                expected = expectedContents.getContentAsString();
            }

            String actual = actualContents.getContentAsString();
            if (matcher == null) {
                if (json) {
                    matcher = jsonEquals(expected);
                } else {
                    matcher = is(expected);
                }
            }
            ContentsAssertions.log.debug("Expected:\n{}", expected);
            ContentsAssertions.log.debug("Actual:\n{}", actual);
            assertThat(message, actual, matcher);
        }
    }

}
