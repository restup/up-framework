package com.github.restup.controller.request.parser.params;

import static com.github.restup.controller.request.parser.params.ComposedRequestParamParser.parameter;

import com.github.restup.test.assertions.Assertions;
import org.junit.Test;

public class ComposedRequestParamParserTest {

    @Test
    public void testRequiredParams() {
        Assertions.assertThrows(() -> parameter().build(), IllegalStateException.class)
            .hasMessage("parameterMatcher is required");
        Assertions.assertThrows(() -> parameter("foo").build(), IllegalStateException.class)
            .hasMessage("parameterValueParser is required");
    }

}
