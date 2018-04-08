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

    public final static boolean tddCheat(final Contents expected, final Contents actual) {
        if (expected instanceof ResourceContents) {
            final ResourceContents resource = (ResourceContents) expected;
            if (!resource.exists()) {
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
    public static Builder builder(final Class<?> unitTest) {
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
    public static Builder assertText(final Class<?> unitTest) {
        return ContentsAssertions.builder(unitTest);
    }

    /**
     * A convenience method to create a {@link Builder} to assert assertJson contents using a
     * specific class for any relative path {@link Contents}
     *
     * @param unitTest to use for relative resources
     * @return a new contents assertions builder
     */
    public static Builder assertJson(final Class<?> unitTest) {
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
    public static Builder assertJson(final ObjectMapper mapper) {
        return ContentsAssertions.assertJson().mapper(mapper);
    }

    /**
     * A convenience method to create a {@link Builder} to assert assertJson contents using a
     * specific {@link Gson} instance
     *
     * @param gson to use for json serialization
     * @return a new contents assertions builder
     */
    public static Builder assertJson(final Gson gson) {
        return ContentsAssertions.assertJson().gson(gson);
    }

    public static class Builder {

        private final Contents.Builder actual;
        private final Contents.Builder expected;
        private Matcher<String> matcher;
        private ResultSerializer serializer;
        private boolean json;

        Builder(final Class<?> unitTest) {
            this.actual = Contents.builder();
            this.actual.testClass(unitTest);
            this.expected = Contents.builder();
            this.expected.testClass(unitTest);
        }

        Builder result(final byte[] body) {
            this.actual.contents(body);
            return this.me();
        }

        Builder result(final String body) {
            this.actual.contents(body);
            return this.me();
        }

        Builder result(final Contents body) {
            this.actual.contents(body);
            return this.me();
        }

        Builder result(final Object value) {
            final String body = this.serialize(value);
            return this.result(body);
        }

        public Builder gson(final Gson gson) {
            return this.serializer(new GsonSerializer(gson));
        }

        public Builder mapper(final ObjectMapper mapper) {
            return this.serializer(new JacksonSerializer(mapper));
        }

        public Builder serializer(final ResultSerializer serializer) {
            this.serializer = serializer;
            return this.me();
        }

        public String serialize(final Object value) {
            if (this.serializer == null) {
                if (AutoDetectConstants.JACKSON2_EXISTS) {
                    this.serializer = new JacksonSerializer();
                } else if (AutoDetectConstants.GSON_EXISTS) {
                    this.serializer = new GsonSerializer();
                } else {
                    throw new IllegalStateException("Unable to serialize value. Please add a supported serializer to classpath (Jackson, Gson))");
                }
            }
            return this.serializer.convertToString(value);
        }

        public Builder expect(final byte[] body) {
            this.expected.contents(body);
            return this.me();
        }

        public Builder expect(final String body) {
            this.expected.contents(body);
            return this.me();
        }

        public Builder expect(final Contents body) {
            this.expected.contents(body);
            return this.me();
        }

        public Builder expect(final Object body) {
            return this.expect(this.serialize(body));
        }

        public Builder matcher(final Matcher<String> matcher) {
            this.matcher = matcher;
            return this.me();
        }

        public Builder json() {
            return this.json(true);
        }

        public Builder json(final boolean json) {
            this.json = json;
            return this.me();
        }

        protected Builder me() {
            return this;
        }

        /**
         * assert expected matches the provided contents
         * 
         * @param contents to match
         */
        public void matches(final byte[] contents) {
            this.result(contents).build();
        }

        /**
         * assert expected matches the provided contents
         * 
         * @param contents to match
         */
        public void matches(final String contents) {
            this.result(contents).build();
        }

        /**
         * assert expected matches the provided contents
         * 
         * @param contents to match
         */
        public void matches(final Contents contents) {
            this.result(contents).build();
        }

        /**
         * assert expected matches the provided contents
         * 
         * @param contents to match
         */
        public void matches(final Object contents) {
            this.result(contents).build();
        }

        /**
         * 
         * @param testName name of test to use.  Used by default for relative resource file names.
         * @return builder this builder
         */
        public Builder test(final String testName) {
            this.actual.testName(testName);
            this.expected.testName(testName);
            return this.me();
        }

        void build() {
            final Contents expectedContents = this.expected.build();
            final Contents actualContents = this.actual.build();


            String expected = null;
            String message = "Contents";
            if (ContentsAssertions.tddCheat(expectedContents, actualContents)) {
                message = "Result has been written for convenience. Verify correctness of results for future executions";
            } else {
                expected = expectedContents.getContentAsString();
            }

            final String actual = actualContents.getContentAsString();
            if (this.matcher == null) {
                if (this.json) {
                    this.matcher = jsonEquals(expected);
                } else {
                    this.matcher = is(expected);
                }
            }
            ContentsAssertions.log.debug("Expected:\n{}", expected);
            ContentsAssertions.log.debug("Actual:\n{}", actual);
            assertThat(message, actual, this.matcher);
        }
    }

}
